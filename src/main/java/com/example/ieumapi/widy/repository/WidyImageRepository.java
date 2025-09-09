package com.example.ieumapi.widy.repository;

import com.example.ieumapi.widy.domain.WidyImage;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WidyImageRepository extends JpaRepository<WidyImage, Long> {
    List<WidyImage> findByWidyId(Long widyId);

    List<WidyImage> findByWidyIdIn(List<Long> widyIds);
}
