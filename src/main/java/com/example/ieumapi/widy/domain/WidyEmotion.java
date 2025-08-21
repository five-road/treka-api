package com.example.ieumapi.widy.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(enumAsRef = true)
public enum WidyEmotion {
    SENSITIVITY("감성"),
    TASTY_PLACE("맛집"),
    SCENIC_PLACE("뷰맛집"),
    OVERWHELMED("압도"),
    SPONTANEOUS("즉흥"),
    EXCITED("신남"),
    HEALING("힐링");

    private final String value;

    WidyEmotion(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static WidyEmotion from(String value) {
        for (WidyEmotion emotion : WidyEmotion.values()) {
            if (emotion.getValue().equals(value)) {
                return emotion;
            }
        }
        return null;
    }
}
