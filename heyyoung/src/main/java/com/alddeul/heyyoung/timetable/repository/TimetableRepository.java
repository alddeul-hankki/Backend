package com.alddeul.heyyoung.timetable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alddeul.heyyoung.timetable.domain.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long>{
	List<Timetable> findByUser_Id(Long userId);
}
