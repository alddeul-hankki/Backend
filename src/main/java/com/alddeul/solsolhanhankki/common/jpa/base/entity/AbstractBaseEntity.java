package com.alddeul.solsolhanhankki.common.jpa.base.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.Hibernate;

/**
 * 모든 엔티티 base 입니다.
 * PK 기반 동등성을 제공합니다.
 * Hibernate 프록시를 실제 엔티티 클래스로 언래핑한 뒤 클래스가 같을 때만 비교합니다.
 * id로만 동등성을 판단하고, 아직 미영속 상태라면 false를 반환합니다.
 */
@MappedSuperclass
@Getter
public abstract class AbstractBaseEntity extends BaseTimeEntity {

    public abstract Object getId();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (Hibernate.getClass(this) != Hibernate.getClass(o)) return false;

        AbstractBaseEntity other = (AbstractBaseEntity) o;
        Object a = this.getId();
        Object b = other.getId();
        return a != null && a.equals(b);
    }

    @Override
    public final int hashCode() {
        return (getId() != null) ? getId().hashCode() : System.identityHashCode(this);
    }
}