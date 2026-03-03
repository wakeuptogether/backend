package com.example.wakeup_back.domain.mission.repository;

import com.example.wakeup_back.domain.mission.entity.Mission;
import com.example.wakeup_back.domain.mission.entity.MissionCompletion;
import com.example.wakeup_back.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MissionCompletionRepository extends JpaRepository<MissionCompletion, Long> {
    Optional<MissionCompletion> findByMissionAndUser(Mission mission, User user);
    List<MissionCompletion> findAllByMission(Mission mission);
    boolean existsByMissionAndUser(Mission mission, User user);
}