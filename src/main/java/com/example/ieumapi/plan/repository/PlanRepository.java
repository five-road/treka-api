package com.example.ieumapi.plan.repository;

import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PlanRepository extends JpaRepository<Plan, Long> {

    // 내 개인 플랜(그룹 null)
    List<Plan> findByOwnerAndGroupIsNullAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(
            User owner, LocalDateTime cursorTime, Pageable pageable
    );

    List<Plan> findByOwnerAndGroupIsNullAndTitleContainingIgnoreCaseAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(
            User owner, String title, LocalDateTime cursorTime, Pageable pageable
    );

    // 특정 그룹의 플랜
    List<Plan> findByGroupAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(
            Group group, LocalDateTime cursorTime, Pageable pageable
    );

    List<Plan> findByGroupAndTitleContainingIgnoreCaseAndCreatedAtLessThanOrderByCreatedAtDescPlanIdDesc(
            Group group, String title, LocalDateTime cursorTime, Pageable pageable
    );
}
