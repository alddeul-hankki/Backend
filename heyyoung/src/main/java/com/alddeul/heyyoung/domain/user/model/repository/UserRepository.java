package com.alddeul.heyyoung.domain.user.model.repository;

import com.alddeul.heyyoung.domain.user.model.entity.SolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SolUser, Long> {
    Optional<SolUser> findByEmail(String email);
}
