package com.example.wakeup_back.domain.group.repository;

import com.example.wakeup_back.domain.group.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByInviteCode(String inviteCode);
}