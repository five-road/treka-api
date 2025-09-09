package com.example.ieumapi.widy.repository;

import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.domain.WidyScope;
import java.util.Collection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface WidyRepository extends JpaRepository<Widy, Long> {


    List<Widy> findByScopeAndCreatedAtLessThanOrderByCreatedAtDesc(WidyScope scope, LocalDateTime createdAt, Pageable pageable);
    List<Widy> findByScopeAndGroupIdInAndCreatedAtLessThanOrderByCreatedAtDesc(
        WidyScope scope,
        Collection<Long> groupId,
        LocalDateTime createdAt,
        Pageable pageable
    );

    List<Widy> findByUserIdAndScopeAndCreatedAtLessThanOrderByCreatedAtDesc(Long userId, WidyScope scope,LocalDateTime createdAt, Pageable pageable);


    @Query("SELECT COUNT(DISTINCT w.widyId) FROM Widy w WHERE w.userId = :userId OR w.groupId IN :groupIds")
    long countVisibleWidysForUser(@Param("userId") Long userId, @Param("groupIds") List<Long> groupIds);

    @Query("SELECT w FROM Widy w WHERE (w.userId = :userId OR w.groupId IN :groupIds) AND w.createdAt >= :startDateTime ORDER BY w.createdAt DESC")
    List<Widy> findVisibleWidysForUser(@Param("userId") Long userId, @Param("groupIds") List<Long> groupIds, @Param("startDateTime") LocalDateTime startDateTime, Pageable pageable);

}
