package com.alddeul.heyyoung.timetable.dto;

import java.time.LocalTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record TimetableLocationRequest(
		long userId,
		int dayOfWeek,
		@JsonFormat(pattern = "HH:mm:ss")
		LocalTime endTime
) {}