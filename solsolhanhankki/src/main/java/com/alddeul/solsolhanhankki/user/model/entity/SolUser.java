package com.alddeul.solsolhanhankki.user.model.entity;

import com.alddeul.solsolhanhankki.common.jpa.base.entity.BaseIdentityEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "sol_user")
public class SolUser extends BaseIdentityEntity {

    @Column(name = "student_id")
    private String studentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 50)
    private String major;

    @Column(nullable = false)
    private String email;

    private String accessKey;


    public void registerUserAtBank(String accessKey) {
        if (accessKey != null) {
            throw new IllegalStateException("이미 은행에 등록된 사용자입니다.");
        }
        this.accessKey = accessKey;
    }
}
