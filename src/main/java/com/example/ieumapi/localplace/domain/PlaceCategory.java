package com.example.ieumapi.localplace.domain;

import java.util.Arrays;

public enum PlaceCategory {
    TOURIST_ATTRACTION(12),
    CULTURAL_FACILITY(14),
    FESTIVAL(15),
    TRAVEL_COURSE(25),
    LEISURE_SPORTS(28),
    ACCOMMODATION(32),
    SHOPPING(38),
    RESTAURANT(39),
    ETC(0); // 나만 아는 장소

    private final int code;

    PlaceCategory(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static PlaceCategory fromCode(long code) {
        return Arrays.stream(PlaceCategory.values())
                .filter(c -> c.code == code)
                .findFirst()
                .orElse(ETC);
    }
}
