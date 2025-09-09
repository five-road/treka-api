package com.example.ieumapi.weather.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WeatherInfo {
    private final String temperature; // 기온
    private final String precipitationProbability; // 강수확률(%)
    private final String skyStatus; // 하늘상태
    private final String precipitationType; // 강수형태
}
