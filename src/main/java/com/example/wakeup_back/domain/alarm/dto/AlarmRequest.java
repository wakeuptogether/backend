package com.example.wakeup_back.domain.alarm.dto;

import lombok.Getter;

@Getter
public class AlarmRequest {
    private String title;
    private int hour;
    private int minute;
    private String repeatDays;
    private boolean isActive;
}