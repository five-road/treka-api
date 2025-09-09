package com.example.ieumapi.weather.client;

import com.example.ieumapi.weather.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kma-api", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
public interface WeatherApiClient {

    @GetMapping("/getVilageFcst")
    WeatherResponse getVilageFcst(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("numOfRows") int numOfRows,
            @RequestParam("dataType") String dataType,
            @RequestParam("base_date") String baseDate,
            @RequestParam("base_time") String baseTime,
            @RequestParam("nx") int nx,
            @RequestParam("ny") int ny
    );
}
