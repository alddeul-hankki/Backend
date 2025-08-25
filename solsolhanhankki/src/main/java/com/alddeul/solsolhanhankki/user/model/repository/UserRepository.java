package com.alddeul.solsolhanhankki.user.model.repository;


import com.alddeul.solsolhanhankki.user.model.entity.SolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<SolUser, Long> {
    Optional<SolUser> findByEmail(String email);
}
