package com.alddeul.heyyoung.timetable.repository;

import java.math.BigDecimal;

public interface TimetableLocationProjection {
	Long getUserId();
	BigDecimal getLongitude();
	BigDecimal getLatitude();
}