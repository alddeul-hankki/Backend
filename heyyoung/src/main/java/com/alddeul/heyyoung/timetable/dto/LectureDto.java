package com.alddeul.heyyoung.timetable.dto;

import java.math.BigDecimal;
import java.time.LocalTime;

import com.alddeul.heyyoung.timetable.domain.Lecture;

public record LectureDto(
		String professor,
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime,
        String name,
        BigDecimal latitude,
        BigDecimal longitude
) {

    public static LectureDto fromEntity(Lecture lecture) {
        return new LectureDto(
        		lecture.getProfessor(),
                lecture.getDayOfWeek(),
                lecture.getStartTime(),
                lecture.getEndTime(),
                lecture.getName(),
                lecture.getLatitude(),
                lecture.getLongitude()
        );
    }
}
