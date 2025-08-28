package com.alddeul.heyyoung.timetable.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alddeul.heyyoung.timetable.dto.LectureDto;
import com.alddeul.heyyoung.timetable.dto.UserTimeTableDto;
import com.alddeul.heyyoung.timetable.repository.TimetableRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TimetableService {

    private final TimetableRepository timetableRepository;

    public List<UserTimeTableDto.Response> getUsersTimetables(List<Integer> uIds) {
        return uIds.stream()
                   .map(uId -> {
                       List<LectureDto> lectures = timetableRepository.findByUser_Id((long) uId)
                                                                  .stream()
                                                                  .map(tt -> LectureDto.fromEntity(tt.getLecture()))
                                                                  .toList();
                       return new UserTimeTableDto.Response(uId, lectures);
                   })
                   .toList();
    }
}

