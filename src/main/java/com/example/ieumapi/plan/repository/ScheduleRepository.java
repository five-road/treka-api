package com.example.ieumapi.plan.repository;

import com.example.ieumapi.plan.domain.Plan;
import com.example.ieumapi.plan.domain.Schedule;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 전체
    List<Schedule> findByPlanAndScheduleIdLessThanOrderByScheduleIdDesc(
            Plan plan, Long cursorId, Pageable pageable
    );

    // 특정 날짜(자정~자정) 범위
    List<Schedule> findByPlanAndStartAtBetweenAndScheduleIdLessThanOrderByScheduleIdDesc(
            Plan plan, LocalDateTime startInclusive, LocalDateTime endExclusive, Long cursorId, Pageable pageable
    );
}
