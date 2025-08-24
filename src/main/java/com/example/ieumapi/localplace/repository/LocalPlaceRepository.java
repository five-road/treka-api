package com.example.ieumapi.localplace.repository;

import com.example.ieumapi.localplace.domain.LocalPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LocalPlaceRepository extends JpaRepository<LocalPlace, Long> {

    @Query(value = "SELECT * FROM local_place p " +
            "WHERE ST_Distance_Sphere(point(p.longitude, p.latitude), point(:longitude, :latitude)) <= :distanceKm * 1000",
            nativeQuery = true)
    List<LocalPlace> findNearbyPlaces(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distanceKm") double distanceKm);
}
