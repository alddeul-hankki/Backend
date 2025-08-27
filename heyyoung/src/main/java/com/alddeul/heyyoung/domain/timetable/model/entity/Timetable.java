package com.alddeul.heyyoung.domain.timetable.model.entity;

import com.alddeul.heyyoung.common.jpa.base.entity.BaseIdentityEntity;
import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "timetable",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_lecture", columnNames = {"user_id", "lecture_id"})
        },
        indexes = {
                @Index(name = "ix_tt_user", columnList = "user_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timetable extends BaseIdentityEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private SolUser user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lecture_id", nullable = false,
            foreignKey = @ForeignKey(name = "timetable_lecture_fk"))
    private Lecture lecture;
}
