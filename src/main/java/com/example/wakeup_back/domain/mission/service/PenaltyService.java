package com.example.wakeup_back.domain.mission.service;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.alarm.repository.AlarmRepository;
import com.example.wakeup_back.domain.user.entity.User;
import com.example.wakeup_back.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    // 벌칙 문구 목록
    private static final List<String> PENALTY_PHRASES = List.of(
            "%s은(는) 아직도 잠자고 있는 귀엽둥이에욤~",
            "%s이(가) 이불 밖은 위험하다고 합니다 🐣",
            "%s은(는) 오늘도 세상 모르고 자는 중이에요 💤",
            "저 %s인데요~ 아직 일어나기 싫어요 🥺",
            "%s의 알람이 5번 울렸지만 아무 소용 없었어요 😴",
            "%s은(는) 오늘 지각 예약 완료했습니다 🚨",
            "긴급속보: %s이(가) 현재도 꿈나라 여행 중입니다 🌙"
    );

    // 랜덤 문구 생성
    public String getPenaltyPhrase(Long alarmId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        String template = PENALTY_PHRASES.get(new Random().nextInt(PENALTY_PHRASES.size()));
        return String.format(template, user.getName());
    }

    // 음성 파일 저장 + WebSocket으로 그룹에 알림
    public String uploadPenaltyVoice(Long alarmId, Long userId, MultipartFile file) throws IOException {
        Alarm alarm = alarmRepository.findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 알람입니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        // 파일 저장
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        String uploadDir = "uploads/voices/";

        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        File dest = new File(uploadDir + fileName);
        file.transferTo(dest);

        String fileUrl = "/voices/" + fileName;

        // WebSocket으로 그룹 멤버들에게 알림
        Long groupId = alarm.getGroup().getId();
        messagingTemplate.convertAndSend(
                "/topic/group/" + groupId + "/penalty",
                (Object) Map.of(
                        "userId", userId,
                        "userName", user.getName(),
                        "voiceUrl", fileUrl,
                        "message", user.getName() + "의 벌칙 음성이 도착했어요! 🎤"
                )
        );

        return fileUrl;
    }
}