package com.example.wakeup_back.domain.group.service;

import com.example.wakeup_back.domain.group.dto.CreateGroupRequest;
import com.example.wakeup_back.domain.group.dto.GroupResponse;
import com.example.wakeup_back.domain.group.dto.JoinGroupRequest;
import com.example.wakeup_back.domain.group.entity.Group;
import com.example.wakeup_back.domain.group.entity.GroupMember;
import com.example.wakeup_back.domain.group.repository.GroupMemberRepository;
import com.example.wakeup_back.domain.group.repository.GroupRepository;
import com.example.wakeup_back.domain.user.entity.User;
import com.example.wakeup_back.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    // 그룹 생성
    public GroupResponse createGroup(Long userId, CreateGroupRequest request) {
        User user = getUser(userId);

        String inviteCode = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Group group = Group.builder()
                .name(request.getName())
                .inviteCode(inviteCode)
                .createdBy(user)
                .build();

        groupRepository.save(group);

        // 생성자도 멤버로 자동 추가
        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();

        groupMemberRepository.save(member);

        return GroupResponse.from(group);
    }

    // 내 그룹 목록
    public List<GroupResponse> getMyGroups(Long userId) {
        User user = getUser(userId);

        return groupMemberRepository.findAllByUser(user).stream()
                .map(gm -> GroupResponse.from(gm.getGroup()))
                .collect(Collectors.toList());
    }

    // 그룹 상세
    public GroupResponse getGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));
        return GroupResponse.from(group);
    }

    // 초대코드로 참가
    public GroupResponse joinGroup(Long userId, JoinGroupRequest request) {
        User user = getUser(userId);

        Group group = groupRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 초대 코드입니다."));

        if (groupMemberRepository.existsByGroupAndUser(group, user)) {
            throw new IllegalArgumentException("이미 참가한 그룹입니다.");
        }

        GroupMember member = GroupMember.builder()
                .group(group)
                .user(user)
                .build();

        groupMemberRepository.save(member);

        return GroupResponse.from(group);
    }

    // 그룹 나가기
    public void leaveGroup(Long userId, Long groupId) {
        User user = getUser(userId);

        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        GroupMember member = groupMemberRepository.findByGroupAndUser(group, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹의 멤버가 아닙니다."));

        groupMemberRepository.delete(member);
    }

    // 그룹 삭제
    public void deleteGroup(Long userId, Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 그룹입니다."));

        if (!group.getCreatedBy().getId().equals(userId)) {
            throw new IllegalArgumentException("그룹 생성자만 삭제할 수 있습니다.");
        }

        // 연관된 멤버들 먼저 삭제 (또는 Cascade 설정에 따라 자동 삭제)
        groupMemberRepository.deleteAllByGroup(group);
        groupRepository.delete(group);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));
    }
}