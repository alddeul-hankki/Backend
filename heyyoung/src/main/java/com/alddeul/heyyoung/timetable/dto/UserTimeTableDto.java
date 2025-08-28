package com.alddeul.heyyoung.timetable.dto;

import java.util.List;

public class UserTimeTableDto {

    public static record Response(
            int userId,
            List<LectureDto> lectures
    ) {}

    public static record ListResponse(
            boolean success,
            String message,
            List<Response> timetables
    ) {
        public static ListResponse success(List<Response> timetables) {
            return new ListResponse(true, "시간표 목록 조회 성공", timetables);
        }

        public static ListResponse fail(String message) {
            return new ListResponse(false, message, null);
        }
    }
}
