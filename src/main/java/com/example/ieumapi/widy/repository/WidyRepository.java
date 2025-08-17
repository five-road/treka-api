package com.example.ieumapi.widy.repository;

import com.example.ieumapi.widy.domain.Widy;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WidyRepository extends JpaRepository<Widy, Long> {
    List<Widy> findByUserId(Long userId);
}

