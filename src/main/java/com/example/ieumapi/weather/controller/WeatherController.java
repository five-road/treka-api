package com.example.ieumapi.weather.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.weather.dto.WeatherResponse;
import com.example.ieumapi.weather.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @Operation(summary = "단기예보 조회", description = "단기예보 정보를 받아옵니다.")
    @GetMapping("/forecast")
    public CommonResponse<WeatherResponse> getShortTermForecast(
            @RequestParam("nx") String nx,
            @RequestParam("ny") String ny
    ) {
        WeatherResponse weatherResponse = weatherService.getShortTermForecast(nx, ny);
        return CommonResponse.success(weatherResponse);
    }
}
