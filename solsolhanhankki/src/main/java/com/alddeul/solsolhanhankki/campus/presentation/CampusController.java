package com.alddeul.solsolhanhankki.campus.presentation;

import com.alddeul.solsolhanhankki.campus.model.entity.PickupZone;
import com.alddeul.solsolhanhankki.campus.model.repository.PickupZoneRepository;
import com.alddeul.solsolhanhankki.campus.presentation.response.PickupZoneResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sol/api/campus")
@RequiredArgsConstructor
public class CampusController {

    private final PickupZoneRepository pickupZoneRepository;

    @GetMapping("/{campusId}/pickup-zones")
    public ResponseEntity<List<PickupZoneResponse>> getPickupZonesByCampus(@PathVariable Long campusId) {
        List<PickupZone> pickupZones = pickupZoneRepository.findByCampus_Id(campusId);

        List<PickupZoneResponse> responses = pickupZones.stream()
                .map(PickupZoneResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }
}