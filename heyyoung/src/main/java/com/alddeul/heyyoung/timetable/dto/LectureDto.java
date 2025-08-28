package com.alddeul.heyyoung.timetable.dto;

import java.time.LocalTime;

public record LectureDto(
        Integer dayOfWeek,
        LocalTime startTime,
        LocalTime endTime
) {

    public static LectureDto fromEntity(com.alddeul.heyyoung.timetable.domain.Lecture lecture) {
        return new LectureDto(
                lecture.getDayOfWeek(),
                lecture.getStartTime(),
                lecture.getEndTime()
        );
    }
}
