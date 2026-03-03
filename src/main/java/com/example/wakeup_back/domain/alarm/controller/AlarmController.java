package com.example.wakeup_back.domain.alarm.controller;

import com.example.wakeup_back.domain.alarm.dto.AlarmRequest;
import com.example.wakeup_back.domain.alarm.dto.AlarmResponse;
import com.example.wakeup_back.domain.alarm.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    // 알람 생성
    @PostMapping("/api/groups/{groupId}/alarms")
    public AlarmResponse createAlarm(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Long groupId,
                                     @RequestBody AlarmRequest request) {
        return alarmService.createAlarm(getUserId(userDetails), groupId, request);
    }

    // 그룹 알람 목록
    @GetMapping("/api/groups/{groupId}/alarms")
    public List<AlarmResponse> getAlarms(@AuthenticationPrincipal UserDetails userDetails,
                                         @PathVariable Long groupId) {
        return alarmService.getAlarms(getUserId(userDetails), groupId);
    }

    // 알람 수정
    @PutMapping("/api/alarms/{alarmId}")
    public AlarmResponse updateAlarm(@AuthenticationPrincipal UserDetails userDetails,
                                     @PathVariable Long alarmId,
                                     @RequestBody AlarmRequest request) {
        return alarmService.updateAlarm(getUserId(userDetails), alarmId, request);
    }

    // 알람 삭제
    @DeleteMapping("/api/alarms/{alarmId}")
    public String deleteAlarm(@AuthenticationPrincipal UserDetails userDetails,
                              @PathVariable Long alarmId) {
        alarmService.deleteAlarm(getUserId(userDetails), alarmId);
        return "알람이 삭제되었습니다.";
    }

    // 알람 토글
    @PatchMapping("/api/alarms/{alarmId}/toggle")
    public Map<String, Boolean> toggleAlarm(@AuthenticationPrincipal UserDetails userDetails,
                                            @PathVariable Long alarmId,
                                            @RequestBody Map<String, Boolean> body) {
        boolean isActive = alarmService.toggleAlarm(getUserId(userDetails), alarmId, body.get("isActive"));
        return Map.of("isActive", isActive);
    }

    private Long getUserId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }
}