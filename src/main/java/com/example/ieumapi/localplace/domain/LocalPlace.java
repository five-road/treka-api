package com.example.ieumapi.localplace.domain;

import io.hypersistence.utils.hibernate.id.Tsid;
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
    @Tsid
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

    @Column(nullable = false)
    private Long contentTypeId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlaceCategory category;


    @Builder
    public LocalPlace(String name,
        String description,
        String address,
        Double latitude,
        Double longitude,
        Long userId,
        String userNickName,
        Source source,
        String ktoContentId,
        Long contentTypeId,
        PlaceCategory category){
        this.name = name;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.userId = userId;
        this.userNickName = userNickName;
        this.source = source;
        this.ktoContentId = ktoContentId;
        this.contentTypeId = contentTypeId;;
        this.category = category;
    }

    public void update(String name, String description, String address, PlaceCategory category) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.category = category;
    }
}
