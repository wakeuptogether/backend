package com.example.wakeup_back.domain.alarm.repository;

import com.example.wakeup_back.domain.alarm.entity.Alarm;
import com.example.wakeup_back.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findAllByGroup(Group group);
    List<Alarm> findAllByIsActiveTrue();
}