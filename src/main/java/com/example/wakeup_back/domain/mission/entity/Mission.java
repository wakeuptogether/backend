package com.example.wakeup_back.domain.mission.entity;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.mission.entity.MissionCompletion;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "missions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "alarm_id")
    private Alarm alarm;

    private String type;
    private String content;
    private String payload;
    private int targetValue;

    // MissionCompletion과의 관계 추가
    @OneToMany(mappedBy = "mission", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MissionCompletion> completions = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @Builder
    public Mission(Alarm alarm, String type, String content, String payload, int targetValue) {
        this.alarm = alarm;
        this.type = type;
        this.content = content;
        this.payload = payload;
        this.targetValue = targetValue;
    }
}