package com.alddeul.heyyoung.timetable.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.alddeul.heyyoung.timetable.dto.LectureDto;
import com.alddeul.heyyoung.timetable.dto.TimetableLocationRequest;
import com.alddeul.heyyoung.timetable.dto.TimetableLocationResponse;
import com.alddeul.heyyoung.timetable.dto.UserTimeTableDto;
import com.alddeul.heyyoung.timetable.repository.TimetableLocationProjection;
import com.alddeul.heyyoung.timetable.repository.TimetableRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;

    @Transactional
    public List<UserTimeTableDto.Response> getUsersTimetables(List<Long> userIds) {
        return userIds.stream()
                   .map(userId -> {
                       List<LectureDto> lectures = timetableRepository.findByUserId(userId)
                                                                  .stream()
                                                                  .map(tt -> LectureDto.fromEntity(tt.getLecture()))
                                                                  .toList();
                       return new UserTimeTableDto.Response(userId, lectures);
                   })
                   .toList();
    }
    
    @Transactional
    public UserTimeTableDto.Response getUserTimetable(long userId) {
        List<LectureDto> lectures = timetableRepository.findByUserId(userId)
                .stream()
                .map(tt -> LectureDto.fromEntity(tt.getLecture()))
                .toList();

        return new UserTimeTableDto.Response(userId, lectures);
    }
    
    @Transactional
	public List<TimetableLocationResponse> findLocations(List<TimetableLocationRequest> requests) {
		List<TimetableLocationResponse> results = new ArrayList<>();
		for (TimetableLocationRequest req : requests) {
			List<TimetableLocationProjection> rows =
					timetableRepository.findLocations(req.userId(), req.dayOfWeek(), req.endTime());
			for (TimetableLocationProjection row : rows) {
				results.add(new TimetableLocationResponse(
						row.getUserId(),
						row.getLongitude(),
						row.getLatitude()
				));
			}
		}
		return results;
	}
}

