package com.example.wakeup_back.domain.alarm.entity;

import com.example.wakeup_back.domain.group.entity.Group;
import com.example.wakeup_back.domain.mission.entity.Mission;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "alarms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private Group group;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private int hour;

    @Column(nullable = false)
    private int minute;

    private String repeatDays;

    @Builder.Default
    private boolean isActive = true;

    @OneToMany(mappedBy = "alarm", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Mission> missions = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    // 알람 수정
    public void update(String title, int hour, int minute, String repeatDays, boolean isActive) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.repeatDays = repeatDays;
        this.isActive = isActive;
    }

    // 활성/비활성 토글
    public void toggle(boolean isActive) {
        this.isActive = isActive;
    }
}