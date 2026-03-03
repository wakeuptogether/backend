package com.example.wakeup_back.domain.group.dto;

import com.example.wakeup_back.domain.group.entity.Group;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupResponse {
    private Long id;
    private String name;
    private String inviteCode;
    private Long createdByUserId;

    public static GroupResponse from(Group group) {
        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .inviteCode(group.getInviteCode())
                .createdByUserId(group.getCreatedBy() != null ? group.getCreatedBy().getId() : null)
                .build();
    }

}