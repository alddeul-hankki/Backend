package com.alddeul.solsolhanhankki.campus.model.repository;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PickupZoneRepository extends JpaRepository<PickupZone, Long> {
}