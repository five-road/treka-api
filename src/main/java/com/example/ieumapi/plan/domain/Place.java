package com.example.ieumapi.plan.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "place")
public class Place extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 255)
    private String address;

    private Double lat; // nullable
    private Double lng; // nullable

    @Column(length = 500)
    private String memo;

    public void update(String name, String address, Double lat, Double lng, String memo) {
        if (name != null) this.name = name;
        if (address != null) this.address = address;
        if (lat != null) this.lat = lat;
        if (lng != null) this.lng = lng;
        if (memo != null) this.memo = memo;
    }
}
