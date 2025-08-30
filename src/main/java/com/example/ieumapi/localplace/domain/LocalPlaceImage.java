package com.example.ieumapi.localplace.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalPlaceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_place_id", nullable = false)
    private LocalPlace localPlace;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = true)
    private String sumNailUrl;

    @Builder
    public LocalPlaceImage(LocalPlace localPlace, String imageUrl) {
        this.localPlace = localPlace;
        this.imageUrl = imageUrl;
    }
}
