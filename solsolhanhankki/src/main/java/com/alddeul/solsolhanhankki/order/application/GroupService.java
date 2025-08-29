package com.alddeul.solsolhanhankki.order.application;

import com.alddeul.solsolhanhankki.order.model.entity.Groups;
import com.alddeul.solsolhanhankki.order.model.enums.GroupStatus;
import com.alddeul.solsolhanhankki.order.model.repository.GroupRepository;
import com.alddeul.solsolhanhankki.order.presentation.response.GroupSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GroupService {

    private final GroupRepository groupRepository;

    public List<GroupSummaryResponse> getRecruitingGroups(Long campusId) {
        List<GroupStatus> statuses = List.of(GroupStatus.RECRUITING, GroupStatus.TRIGGERED);

        List<Groups> availableGroups = groupRepository.findAllByStatusInAndPickupZone_Campus_Id(statuses, campusId);

        return availableGroups.stream()
                .map(GroupSummaryResponse::from)
                .collect(Collectors.toList());
    }
}