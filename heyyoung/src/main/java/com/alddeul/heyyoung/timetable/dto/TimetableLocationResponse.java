package com.alddeul.heyyoung.timetable.dto;

import java.math.BigDecimal;

public record TimetableLocationResponse(
		long userId,
		BigDecimal longitude,
		BigDecimal latitude
) {}