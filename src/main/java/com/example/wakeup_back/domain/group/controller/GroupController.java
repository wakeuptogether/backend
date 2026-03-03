package com.example.wakeup_back.domain.group.controller;

import com.example.wakeup_back.domain.group.dto.CreateGroupRequest;
import com.example.wakeup_back.domain.group.dto.GroupResponse;
import com.example.wakeup_back.domain.group.dto.JoinGroupRequest;
import com.example.wakeup_back.domain.group.service.GroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public GroupResponse createGroup(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateGroupRequest request) {
        return groupService.createGroup(getUserId(userDetails), request);
    }

    @GetMapping
    public List<GroupResponse> getMyGroups(@AuthenticationPrincipal UserDetails userDetails) {
        return groupService.getMyGroups(getUserId(userDetails));
    }

    @GetMapping("/{id}")
    public GroupResponse getGroup(@PathVariable Long id) {
        return groupService.getGroup(id);
    }

    @PostMapping("/join")
    public GroupResponse joinGroup(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody JoinGroupRequest request) {
        return groupService.joinGroup(getUserId(userDetails), request);
    }

    @DeleteMapping("/{id}/leave")
    public String leaveGroup(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        groupService.leaveGroup(getUserId(userDetails), id);
        return "그룹에서 나갔습니다.";
    }

    @DeleteMapping("/{id}")
    public String deleteGroup(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        groupService.deleteGroup(getUserId(userDetails), id);
        return "그룹이 삭제되었습니다.";
    }

    // JWT에서 꺼낸 userId (UserDetails의 username에 userId를 담았었음)
    private Long getUserId(UserDetails userDetails) {
        return Long.parseLong(userDetails.getUsername());
    }
}