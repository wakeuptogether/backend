package com.example.wakeup_back.domain.alarm.service;

import com.example.wakeup_back.domain.alarm.dto.AlarmRequest;
import com.example.wakeup_back.domain.alarm.dto.AlarmResponse;
import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.alarm.repository.AlarmRepository;
import com.example.wakeup_back.domain.group.entity.Group;
import com.example.wakeup_back.domain.group.entity.GroupMember;
import com.example.wakeup_back.domain.group.repository.GroupMemberRepository;
import com.example.wakeup_back.domain.group.repository.GroupRepository;
import com.example.wakeup_back.domain.user.entity.User;
import com.example.wakeup_back.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    // 알람 생성
    public AlarmResponse createAlarm(Long userId, Long groupId, AlarmRequest request) {
        Group group = getGroup(groupId);
        validateCreator(userId, group); // 그룹 생성자인지 확인

        Alarm alarm = Alarm.builder()
                .group(group)
                .title(request.getTitle())
                .hour(request.getHour())
                .minute(request.getMinute())
                .repeatDays(request.getRepeatDays())
                .build();

        alarmRepository.save(alarm);
        return AlarmResponse.from(alarm);
    }

    // 그룹 알람 목록
    public List<AlarmResponse> getAlarms(Long userId, Long groupId) {
        Group group = getGroup(groupId);
        validateMember(userId, group);

        return alarmRepository.findAllByGroup(group).stream()
                .map(AlarmResponse::from)
                .collect(Collectors.toList());
    }

    // 알람 수정
    public AlarmResponse updateAlarm(Long userId, Long alarmId, AlarmRequest request) {
        Alarm alarm = getAlarm(alarmId);
        validateCreator(userId, alarm.getGroup());

        alarm.update(request.getTitle(), request.getHour(), request.getMinute(),
                request.getRepeatDays(), request.isActive());

        alarmRepository.save(alarm);
        return AlarmResponse.from(alarm);
    }

    // 알람 삭제
    public void deleteAlarm(Long userId, Long alarmId) {
        Alarm alarm = getAlarm(alarmId);
        validateCreator(userId, alarm.getGroup());
        alarmRepository.delete(alarm);
    }

    // 알람 활성/비활성 토글
    public boolean toggleAlarm(Long userId, Long alarmId, boolean isActive) {
        Alarm alarm = getAlarm(alarmId);
        validateCreator(userId, alarm.getGroup());
        alarm.toggle(isActive);
        alarmRepository.save(alarm);
        return alarm.isActive();
    }

    // --- 공통 메서드 ---

    private Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
    }

    private Alarm getAlarm(Long alarmId) {
        return alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알람입니다."));
    }

    private void validateMember(Long userId, Group group) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        if (!groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("해당 그룹의 멤버가 아닙니다.");
        }
    }

    private void validateCreator(Long userId, Group group) {
        if (!group.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("그룹 생성자만 관리할 수 있습니다.");
        }
    }
}