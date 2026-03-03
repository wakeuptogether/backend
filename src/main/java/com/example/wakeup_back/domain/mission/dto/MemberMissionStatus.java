package com.example.wakeup_back.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberMissionStatus {
    private Long userId;
    private String name;
    private boolean completed;
    private String completedAt;
}