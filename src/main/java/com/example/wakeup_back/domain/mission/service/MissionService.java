package com.example.wakeup_back.domain.mission.service;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.alarm.repository.AlarmRepository;
import com.example.wakeup_back.domain.group.entity.GroupMember;
import com.example.wakeup_back.domain.group.repository.GroupMemberRepository;
import com.example.wakeup_back.domain.mission.dto.MemberMissionStatus;
import com.example.wakeup_back.domain.mission.dto.MissionResponse;
import com.example.wakeup_back.domain.mission.entity.Mission;
import com.example.wakeup_back.domain.mission.entity.MissionCompletion;
import com.example.wakeup_back.domain.mission.repository.MissionCompletionRepository;
import com.example.wakeup_back.domain.mission.repository.MissionRepository;
import com.example.wakeup_back.domain.user.entity.User;
import com.example.wakeup_back.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionCompletionRepository missionCompletionRepository;
    private final AlarmRepository alarmRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    private static final Random RANDOM = new Random();

    // 타이핑 문구 목록
    private static final List<String> GIBBERISH_POOL = List.of(
            "일어나야해요지금당장",
            "잠은내일자면돼요",
            "오늘도파이팅입니다",
            "눈을떠요제발요",
            "알람을끄면안돼요",
            "기상기상기상기상",
            "늦으면안되잖아요"
    );

    // 랜덤 미션 데이터 생성
    private Mission buildRandomMission(Alarm alarm) {
        int typeIdx = RANDOM.nextInt(5); // 0~4 중 랜덤

        String type, content, payload;
        int targetValue;

        switch (typeIdx) {
            case 0 -> {
                // SHAKE
                type = "SHAKE";
                content = "폰을 흔들어서 잠을 깨워요!";
                payload = "";
                targetValue = 20 + RANDOM.nextInt(21); // 20~40회
            }
            case 1 -> {
                // TAP
                type = "TAP";
                content = "화면을 탭해서 정신을 차려요!";
                payload = "";
                targetValue = 30 + RANDOM.nextInt(31); // 30~60회
            }
            case 2 -> {
                // TYPE_GIBBERISH
                type = "TYPE_GIBBERISH";
                content = "아래 글자를 정확히 입력하세요!";
                payload = GIBBERISH_POOL.get(RANDOM.nextInt(GIBBERISH_POOL.size()));
                targetValue = 1;
            }
            case 3 -> {
                // MATH — 랜덤 수식 생성
                type = "MATH";
                content = "수학 문제를 풀어요!";
                int a = 10 + RANDOM.nextInt(41); // 10~50
                int b = 10 + RANDOM.nextInt(41); // 10~50
                String[] ops = {"+", "-", "×"};
                String op = ops[RANDOM.nextInt(ops.length)];
                payload = a + " " + op + " " + b;
                targetValue = 1;
            }
            default -> {
                // PATTERN — 랜덤 패턴 생성 (0~8 숫자 중 4~5개)
                type = "PATTERN";
                content = "패턴 순서를 기억하고 따라하세요!";
                List<Integer> cells = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8));
                Collections.shuffle(cells);
                int patternLen = 4 + RANDOM.nextInt(2); // 4~5개
                payload = cells.subList(0, patternLen).stream()
                        .map(String::valueOf)
                        .collect(Collectors.joining(","));
                targetValue = 3; // 3번 반복
            }
        }

        return Mission.builder()
                .alarm(alarm)
                .type(type)
                .content(content)
                .payload(payload)
                .targetValue(targetValue)
                .build();
    }

    // 알람 울릴 때 랜덤 미션 받기
    public MissionResponse getRandomMission(Long alarmId) {
        Alarm alarm = getAlarm(alarmId);

        // 가장 최근에 생성된 미션 반환 (내림차순 정렬 필요)
        List<Mission> existing = missionRepository.findAllByAlarmOrderByCreatedAtDesc(alarm);
        if (!existing.isEmpty()) {
            return MissionResponse.from(existing.get(0));
        }

        Mission mission = buildRandomMission(alarm);
        missionRepository.save(mission);
        return MissionResponse.from(mission);
    }

    // 알람 스케줄러에서 자동으로 미션 생성
    public Mission createMissionForAlarm(Alarm alarm) {
        Mission mission = buildRandomMission(alarm);
        return missionRepository.save(mission);
    }

    // 미션 완료 보고
    public MissionCompletion completeMission(Long alarmId, Long userId) {
        Alarm alarm = getAlarm(alarmId);
        User user = getUser(userId);

        List<Mission> missions = missionRepository.findAllByAlarm(alarm);
        if (missions.isEmpty()) {
            throw new IllegalArgumentException("해당 알람에 미션이 없습니다.");
        }

        Mission mission = missions.get(0);

        if (missionCompletionRepository.existsByMissionAndUser(mission, user)) {
            throw new IllegalArgumentException("이미 완료한 미션입니다.");
        }

        MissionCompletion completion = MissionCompletion.builder()
                .mission(mission)
                .user(user)
                .completedAt(LocalDateTime.now())
                .build();

        return missionCompletionRepository.save(completion);
    }

    // 그룹 멤버 미션 상태 조회
    public List<MemberMissionStatus> getMissionStatus(Long alarmId) {
        Alarm alarm = getAlarm(alarmId);

        List<Mission> missions = missionRepository.findAllByAlarm(alarm);
        if (missions.isEmpty()) {
            return List.of(); // 미션 없으면 빈 배열 반환
        }

        Mission mission = missions.get(0);
        List<GroupMember> members = groupMemberRepository.findAllByGroup(alarm.getGroup());

        return members.stream().map(gm -> {
            User user = gm.getUser();
            boolean completed = missionCompletionRepository.existsByMissionAndUser(mission, user);
            String completedAt = null;

            if (completed) {
                completedAt = missionCompletionRepository
                        .findByMissionAndUser(mission, user)
                        .map(c -> c.getCompletedAt().toString())
                        .orElse(null);
            }

            return MemberMissionStatus.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .completed(completed)
                    .completedAt(completedAt)
                    .build();
        }).collect(Collectors.toList());
    }

    // 미션 미완료 유저 목록 (AlarmScheduler에서 사용)
    public List<User> getIncompleteMissionUsers(Alarm alarm) {
        List<Mission> missions = missionRepository.findAllByAlarm(alarm);
        if (missions.isEmpty()) return List.of();

        Mission mission = missions.get(0);
        List<GroupMember> members = groupMemberRepository.findAllByGroup(alarm.getGroup());

        return members.stream()
                .map(GroupMember::getUser)
                .filter(user -> !missionCompletionRepository.existsByMissionAndUser(mission, user))
                .collect(Collectors.toList());
    }

    private Alarm getAlarm(Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알람입니다."));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}