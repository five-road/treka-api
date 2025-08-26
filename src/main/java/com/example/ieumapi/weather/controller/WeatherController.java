package com.example.ieumapi.weather.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.weather.dto.WeatherResponseDto;
import com.example.ieumapi.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "제주도 단기예보 조회")
    @GetMapping
    public CommonResponse<WeatherResponseDto> getWeather() {
        WeatherResponseDto weatherResponseDto = weatherService.getWeather();
        return CommonResponse.success(weatherResponseDto);
    }
}
