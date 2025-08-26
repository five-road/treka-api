package com.example.ieumapi.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WeatherResponseDto {
    private final String temperature;
    private final String skyCondition;
    private final String precipitationProbability;
}
