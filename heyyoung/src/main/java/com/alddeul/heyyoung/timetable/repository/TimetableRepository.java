package com.alddeul.heyyoung.timetable.repository;

import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alddeul.heyyoung.timetable.domain.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long>{
	List<Timetable> findByUserId(Long userId);
	
	@Query(value = """
			SELECT t.user_id AS userId, l.longitude AS longitude, l.latitude AS latitude
			FROM lecture l
			JOIN timetable t ON l.id = t.lecture_id
			WHERE t.user_id = :userId
			  AND l.day_of_week = :dayOfWeek
			  AND l.end_time = :endTime
			""", nativeQuery = true)
		List<TimetableLocationProjection> findLocations(
				@Param("userId") long userId,
				@Param("dayOfWeek") int dayOfWeek,
				@Param("endTime") LocalTime endTime
		);
}
