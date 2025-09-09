package com.example.ieumapi.plan.repository;

import com.example.ieumapi.plan.domain.Place;
import com.example.ieumapi.plan.domain.Plan;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    List<Place> findByPlanAndPlaceIdLessThanOrderByPlaceIdDesc(
            Plan plan, Long cursorId, Pageable pageable
    );

    List<Place> findByPlanAndNameContainingIgnoreCaseAndPlaceIdLessThanOrderByPlaceIdDesc(
            Plan plan, String name, Long cursorId, Pageable pageable
    );
}
