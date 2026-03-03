package com.example.wakeup_back.domain.alarm.dto;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AlarmResponse {
    private Long alarmId;
    private Long groupId;
    private String title;
    private int hour;
    private int minute;
    private String repeatDays;
    private boolean isActive;

    public static AlarmResponse from(Alarm alarm) {
        return AlarmResponse.builder()
                .alarmId(alarm.getId())
                .groupId(alarm.getGroup().getId())
                .title(alarm.getTitle())
                .hour(alarm.getHour())
                .minute(alarm.getMinute())
                .repeatDays(alarm.getRepeatDays())
                .isActive(alarm.isActive())
                .build();
    }
}