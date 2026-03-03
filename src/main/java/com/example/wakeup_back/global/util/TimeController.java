package com.example.wakeup_back.global.util;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/time")
public class TimeController {

    // 현재 서버 시간 반환
    @GetMapping("/current")
    public Map<String, Object> getCurrentTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        return Map.of(
                "datetime", now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "hour", now.getHour(),
                "minute", now.getMinute(),
                "second", now.getSecond(),
                "timezone", "Asia/Seoul"
        );
    }
}