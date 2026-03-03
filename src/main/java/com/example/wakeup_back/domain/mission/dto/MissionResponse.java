package com.example.wakeup_back.domain.mission.dto;

import com.example.wakeup_back.domain.mission.entity.Mission;
import lombok.Builder;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MissionResponse {
    private Long id;
    private String type;
    private String label;
    private String description;
    private String payload;
    private int targetValue;
    private List<Integer> patternSequence;

    public static MissionResponse from(Mission mission) {
        List<Integer> patternSequence = null;

        // PATTERN 타입이면 payload("0,4,8,2,6")를 List<Integer>로 변환
        if ("PATTERN".equals(mission.getType()) && mission.getPayload() != null) {
            patternSequence = Arrays.stream(mission.getPayload().split(","))
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
        }

        return MissionResponse.builder()
                .id(mission.getId())
                .type(mission.getType())
                .label(getLabelByType(mission.getType()))
                .description(mission.getContent())
                .payload(mission.getPayload())
                .targetValue(mission.getTargetValue())
                .patternSequence(patternSequence)
                .build();
    }

    private static String getLabelByType(String type) {
        return switch (type) {
            case "SHAKE" -> "폰을 흔들어요!";
            case "TAP" -> "화면을 탭해요!";
            case "TYPE_GIBBERISH" -> "글자를 입력해요!";
            case "MATH" -> "수학 문제를 풀어요!";
            case "PATTERN" -> "패턴을 기억해요!";
            default -> "미션 완료!";
        };
    }
}