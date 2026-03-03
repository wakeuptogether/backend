package com.example.wakeup_back.domain.alarm.service;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.alarm.repository.AlarmRepository;
import com.example.wakeup_back.domain.mission.entity.Mission;
import com.example.wakeup_back.domain.mission.service.MissionService;
import com.example.wakeup_back.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AlarmScheduler {

    private final AlarmRepository alarmRepository;
    private final MissionService missionService;
    private final SimpMessagingTemplate messagingTemplate;

    // 매 분 0초마다 실행
    @Scheduled(cron = "0 * * * * *")
    public void checkAlarms() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        String today = now.getDayOfWeek().name().substring(0, 3); // MON, TUE...

        List<Alarm> activeAlarms = alarmRepository.findAllByIsActiveTrue();
        for (Alarm alarm : activeAlarms) {
            if (alarm.getHour() == hour && alarm.getMinute() == minute) {
                if (alarm.getRepeatDays() == null || alarm.getRepeatDays().contains(today)) {
                    log.info("알람 울림 - alarmId: {}, groupId: {}",
                            alarm.getId(), alarm.getGroup().getId());

                    // 미션 자동 생성
                    Mission mission = missionService.createMissionForAlarm(alarm);
                    log.info("미션 생성 - missionId: {}, content: {}",
                            mission.getId(), mission.getContent());
                }
            }
        }
    }

    // checkTimeout() 메서드 안에 추가
    @Scheduled(cron = "0 * * * * *")
    public void checkTimeout() {
        LocalDateTime now = LocalDateTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();
        String today = now.getDayOfWeek().name().substring(0, 3);

        List<Alarm> activeAlarms = alarmRepository.findAllByIsActiveTrue();

        for (Alarm alarm : activeAlarms) {
            int alarmMinute = alarm.getMinute() + 5;
            int alarmHour = alarm.getHour();

            if (alarmMinute >= 60) {
                alarmMinute -= 60;
                alarmHour += 1;
            }

            if (alarmHour == hour && alarmMinute == minute) {
                if (alarm.getRepeatDays() == null || alarm.getRepeatDays().contains(today)) {
                    List<User> incompleteUsers = missionService.getIncompleteMissionUsers(alarm);

                    if (!incompleteUsers.isEmpty()) {
                        // 미완료 유저들에게 WebSocket으로 벌칙 시작 알림
                        for (User user : incompleteUsers) {
                            String payload = "{\"userId\":" + user.getId() +
                                    ",\"userName\":\"" + user.getName() +
                                    "\",\"message\":\"벌칙 시작! 문구를 읽어주세요 🎤\"}";

                            messagingTemplate.convertAndSend(
                                    "/topic/group/" + alarm.getGroup().getId() + "/penalty-start",
                                    payload
                            );
                            log.info("벌칙 시작 - userId: {}, alarmId: {}", user.getId(), alarm.getId());
                        }
                    }
                }
            }
        }
    }
}