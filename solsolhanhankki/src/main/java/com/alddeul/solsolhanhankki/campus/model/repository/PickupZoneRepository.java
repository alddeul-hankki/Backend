package com.alddeul.solsolhanhankki.campus.model.repository;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PickupZoneRepository extends JpaRepository<PickupZone, Long> {
    List<PickupZone> findByCampus_Id(Long campusId);
}