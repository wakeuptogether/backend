package com.example.wakeup_back.domain.mission.controller;

import com.example.wakeup_back.domain.mission.dto.MemberMissionStatus;
import com.example.wakeup_back.domain.mission.dto.MissionResponse;
import com.example.wakeup_back.domain.mission.entity.MissionCompletion;
import com.example.wakeup_back.domain.mission.service.MissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms/{alarmId}/mission")
public class MissionController {

    private final MissionService missionService;

    // 랜덤 미션 받기
    @GetMapping
    public MissionResponse getRandomMission(@PathVariable Long alarmId) {
        return missionService.getRandomMission(alarmId);
    }

    // 미션 완료 보고
    @PostMapping("/complete")
    public Map<String, Object> completeMission(@PathVariable Long alarmId,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        MissionCompletion completion = missionService.completeMission(
                alarmId, Long.parseLong(userDetails.getUsername()));

        return Map.of(
                "success", true,
                "completedAt", completion.getCompletedAt().toString()
        );
    }

    // 그룹 멤버 미션 상태 조회
    @GetMapping("/status")
    public List<MemberMissionStatus> getMissionStatus(@PathVariable Long alarmId) {
        return missionService.getMissionStatus(alarmId);
    }
}