package com.example.ieumapi.weather.client;

import com.example.ieumapi.weather.dto.WeatherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "weather-api", url = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0")
@FeignClient(name = "weather-api", url = "https://apihub.kma.go.kr/api/typ01/url/fct_shrt_reg.php")
public interface WeatherApiClient {

    @GetMapping("/getVilageFcst")
    WeatherResponse getShortTermForecast(
            @RequestParam("authKey") String authKey,
            @RequestParam("pageNo") int pageNo,
            @RequestParam("numOfRows") int numOfRows,
            @RequestParam("dataType") String dataType,
            @RequestParam("base_date") String baseDate,
            @RequestParam("base_time") String baseTime,
            @RequestParam("nx") String nx,
            @RequestParam("ny") String ny
    );
}
