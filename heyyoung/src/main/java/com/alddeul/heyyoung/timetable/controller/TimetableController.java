package com.alddeul.heyyoung.timetable.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alddeul.heyyoung.timetable.dto.TimetableLocationRequest;
import com.alddeul.heyyoung.timetable.dto.TimetableLocationResponse;
import com.alddeul.heyyoung.timetable.dto.UserTimeTableDto;
import com.alddeul.heyyoung.timetable.service.TimetableService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final TimetableService timetableService;

    @PostMapping("/users")
    public ResponseEntity<UserTimeTableDto.ListResponse> getUsersTimetable(
            @RequestBody List<Long> userIds) {
    	log.info("{}", "요청 들어옴");
        try {
            List<UserTimeTableDto.Response> timetables = timetableService.getUsersTimetables(userIds);
            return ResponseEntity.ok(UserTimeTableDto.ListResponse.success(timetables));
        } catch (Exception e) {
            log.error("시간표 조회 실패", e);
            return ResponseEntity.ok(UserTimeTableDto.ListResponse.fail("시간표 조회 실패"));
        }
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<UserTimeTableDto.ListResponse> getUserTimetable(
            @PathVariable(name = "userId") long userId) {
        try {
            UserTimeTableDto.Response timetable = timetableService.getUserTimetable(userId);
            return ResponseEntity.ok(UserTimeTableDto.ListResponse.success(List.of(timetable)));
        } catch (Exception e) {
            log.error("시간표 조회 실패", e);
            return ResponseEntity.ok(UserTimeTableDto.ListResponse.fail("시간표 조회 실패"));
        }
    }
    
    @PostMapping("/users/locations")
    public ResponseEntity<List<TimetableLocationResponse>> findLocations(
    		@RequestBody List<TimetableLocationRequest> requests
    ) {
    	try {
    	List<TimetableLocationResponse> resp = timetableService.findLocations(requests);
    	return ResponseEntity.ok(resp);
    	}catch(Exception e) {
    		log.error("위치 조회 실패", e);
    		return ResponseEntity.ok(null);
    	}
    }
}
