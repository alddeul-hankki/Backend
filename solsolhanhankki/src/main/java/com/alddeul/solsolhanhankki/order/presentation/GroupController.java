package com.alddeul.solsolhanhankki.order.presentation;

import com.alddeul.solsolhanhankki.order.application.GroupService;
import com.alddeul.solsolhanhankki.order.presentation.request.GroupSummaryRequest;
import com.alddeul.solsolhanhankki.order.presentation.response.GroupSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sol/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @PostMapping
    public ResponseEntity<List<GroupSummaryResponse>> getGroups(
            @RequestBody GroupSummaryRequest  groupSummaryRequest
            ) {
        List<GroupSummaryResponse> groups = groupService.getRecruitingGroups(groupSummaryRequest.campusId());
        return ResponseEntity.ok(groups);
    }
}