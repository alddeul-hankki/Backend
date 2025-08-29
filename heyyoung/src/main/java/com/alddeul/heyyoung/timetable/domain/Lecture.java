package com.alddeul.heyyoung.timetable.domain;

import java.math.BigDecimal;
import java.time.LocalTime;

import org.hibernate.annotations.Check;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "lecture",
    indexes = {
        @Index(name = "ix_lecture_time", columnList = "day_of_week, start_time"),
        @Index(name = "ix_lecture_code", columnList = "code")
    }
)
@Check(constraints = "day_of_week BETWEEN 0 AND 6 AND end_time > start_time")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lecture extends BaseIdentityEntity {

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "code", length = 40)
    private String code;

    @Column(name = "professor", length = 100)
    private String professor;

    @Column(name = "day_of_week", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "latitude", precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(name = "longitude", precision = 9, scale = 6)
    private BigDecimal longitude;
}
