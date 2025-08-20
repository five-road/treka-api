package com.example.ieumapi.plan.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "schedule")
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDateTime startAt;

    @Column(nullable = false)
    private LocalDateTime endAt;

    // 장소(옵션)
    @Column(length = 120)
    private String placeName;

    @Column(length = 255)
    private String address;

    private Double lat;
    private Double lng;

    public void update(
            String title,
            String description,
            LocalDateTime startAt,
            LocalDateTime endAt,
            String placeName,
            String address,
            Double lat,
            Double lng
    ) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (startAt != null) this.startAt = startAt;
        if (endAt != null) this.endAt = endAt;
        if (placeName != null) this.placeName = placeName;
        if (address != null) this.address = address;
        if (lat != null) this.lat = lat;
        if (lng != null) this.lng = lng;
    }
}
