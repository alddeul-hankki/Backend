// GroupRepository.java (최종)
package com.alddeul.solsolhanhankki.order.model.repository;

import com.alddeul.solsolhanhankki.order.model.entity.Groups;
import com.alddeul.solsolhanhankki.order.model.enums.GroupStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface GroupRepository extends JpaRepository<Groups, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select g from Groups g where g.id = :groupId")
    Optional<Groups> findByIdWithPessimisticLock(Long groupId);

    // 특정 가게, 픽업존, 마감 시간에 해당하는 모집중인 그룹을 찾는 쿼리
    Optional<Groups> findByStoreIdAndPickupZoneIdAndDeadlineAtAndStatus(String storeId, Long pickupZoneId, OffsetDateTime deadlineAt, GroupStatus status);

    @Query("select g from Groups g where g.storeId = :storeId and g.pickupZone.id = :pickupZoneId and g.deadlineAt = :deadlineAt and g.status in (com.alddeul.solsolhanhankki.order.model.enums.GroupStatus.RECRUITING, com.alddeul.solsolhanhankki.order.model.enums.GroupStatus.TRIGGERED)")
    Optional<Groups> findAvailableGroupBy(String storeId, Long pickupZoneId, OffsetDateTime deadlineAt);

    List<Groups> findAllByStatusAndTriggeredAtBefore(GroupStatus status, OffsetDateTime threshold);

    List<Groups> findAllByStatusInAndPickupZone_Campus_Id(List<GroupStatus> statuses, Long campusId);

}