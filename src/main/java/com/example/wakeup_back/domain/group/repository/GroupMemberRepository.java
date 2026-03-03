package com.example.wakeup_back.domain.group.repository;

import com.example.wakeup_back.domain.group.entity.Group;
import com.example.wakeup_back.domain.group.entity.GroupMember;
import com.example.wakeup_back.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findAllByGroup(Group group);

    List<GroupMember> findAllByUser(User user);

    Optional<GroupMember> findByGroupAndUser(Group group, User user);

    boolean existsByGroupAndUser(Group group, User user);

    void deleteAllByGroup(Group group);
}