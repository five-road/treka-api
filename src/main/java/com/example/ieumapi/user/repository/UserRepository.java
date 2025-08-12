package com.example.ieumapi.user.repository;

import com.example.ieumapi.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByNickNameContainingIgnoreCaseOrNameContainingIgnoreCaseAndUserIdGreaterThanOrderByUserIdAsc(
            String nickname,
            String name,
            Long cursorId,
            Pageable pageable
    );
}

