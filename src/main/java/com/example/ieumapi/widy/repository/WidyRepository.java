package com.example.ieumapi.widy.repository;

import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.domain.WidyScope;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WidyRepository extends JpaRepository<Widy, Long> {
    List<Widy> findByUserId(Long userId);
    List<Widy> findByScopeAndCreatedAtLessThanOrderByCreatedAtDesc(WidyScope scope, LocalDateTime createdAt, Pageable pageable);
    List<Widy> findByGroupIdInAndCreatedAtLessThanOrderByCreatedAtDesc(List<Long> groupIds, LocalDateTime createdAt, Pageable pageable);
    List<Widy> findByUserIdAndCreatedAtLessThanOrderByCreatedAtDesc(Long userId, LocalDateTime createdAt, Pageable pageable);
}
