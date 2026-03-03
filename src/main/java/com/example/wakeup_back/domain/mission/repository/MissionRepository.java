package com.example.wakeup_back.domain.mission.repository;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.mission.entity.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MissionRepository extends JpaRepository<Mission, Long> {
    List<Mission> findAllByAlarm(Alarm alarm);

    List<Mission> findAllByAlarmOrderByCreatedAtDesc(Alarm alarm);
}