package com.alddeul.heyyoung.common.jpa.base.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Getter
@MappedSuperclass
public abstract class BaseSeqEntity extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "hibernate_sequence", allocationSize = 50)
    @Column(name = "id")
    protected Long id;

    @Override
    public Long getId() {
        return id;
    }
}