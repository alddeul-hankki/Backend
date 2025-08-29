package com.alddeul.heyyoung.timetable.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.alddeul.heyyoung.timetable.dto.LectureDto;
import com.alddeul.heyyoung.timetable.dto.UserTimeTableDto;
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
}

