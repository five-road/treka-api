package com.example.ieumapi.localplace.repository;

import com.example.ieumapi.localplace.domain.LocalPlaceImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocalPlaceImageRepository extends JpaRepository<LocalPlaceImage, Long> {
    List<LocalPlaceImage> findByLocalPlace_PlaceIdIn(List<Long> placeIds);
}
