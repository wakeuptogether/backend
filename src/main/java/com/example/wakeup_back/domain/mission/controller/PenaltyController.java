package com.example.wakeup_back.domain.mission.controller;

import com.example.wakeup_back.domain.mission.service.PenaltyService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alarms/{alarmId}/penalty")
public class PenaltyController {

    private final PenaltyService penaltyService;

    // 랜덤 문구 받기
    @GetMapping("/phrase")
    public Map<String, String> getPenaltyPhrase(
            @PathVariable Long alarmId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String phrase = penaltyService.getPenaltyPhrase(
                alarmId, Long.parseLong(userDetails.getUsername()));

        return Map.of("phrase", phrase);
    }

    // 음성 파일 업로드
    @PostMapping(value = "/voice", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, String> uploadVoice(
            @PathVariable Long alarmId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestPart("file") MultipartFile file) throws IOException {

        String voiceUrl = penaltyService.uploadPenaltyVoice(
                alarmId, Long.parseLong(userDetails.getUsername()), file);

        return Map.of("voiceUrl", voiceUrl);
    }

    // 음성 파일 다운로드
    @GetMapping("/voice/{fileName}")
    public ResponseEntity<Resource> getVoice(@PathVariable String fileName) {
        Resource resource = new FileSystemResource("uploads/voices/" + fileName);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/webm"))
                .body(resource);
    }
}