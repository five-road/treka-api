package com.example.ieumapi.localplace.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalPlace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long placeId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 500)
    private String description;

    private String address;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String userNickName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Source source; // 데이터 출처 (USER, KTO)

    @Column(unique = true)
    private String ktoContentId; // 한국관광공사 contentId, nullable

    @OneToMany(mappedBy = "localPlace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocalPlaceImage> images = new ArrayList<>();

    @Builder
    public LocalPlace(String name, String description, String address, Double latitude, Double longitude, Long userId, String userNickName, Source source, String ktoContentId) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.userNickName = userNickName;
        this.source = source;
        this.ktoContentId = ktoContentId;
    }

    public void update(String name, String description, String address) {
        this.name = name;
        this.description = description;
        this.address = address;
    }
}
