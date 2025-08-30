package com.example.ieumapi.localplace.repository;

import com.example.ieumapi.localplace.domain.LocalPlace;
import com.example.ieumapi.localplace.domain.Source;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface LocalPlaceRepository extends JpaRepository<LocalPlace, Long> {

    Optional<LocalPlace> findByKtoContentId(String ktoContentId);

    List<LocalPlace> findByNameContainingIgnoreCaseAndSource(String name, Source source);

    @Query(value = "SELECT * FROM local_place p " +
            "WHERE ST_Distance_Sphere(point(p.longitude, p.latitude), point(:longitude, :latitude)) <= :distanceKm * 1000",
            nativeQuery = true)
    List<LocalPlace> findNearbyPlaces(@Param("latitude") double latitude, @Param("longitude") double longitude, @Param("distanceKm") double distanceKm);
}
