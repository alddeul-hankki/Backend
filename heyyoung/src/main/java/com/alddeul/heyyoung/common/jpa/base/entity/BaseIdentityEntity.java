package com.alddeul.heyyoung.common.jpa.base.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseIdentityEntity extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    protected Long id;

    @Override
    public Long getId() {
        return id;
    }
}
