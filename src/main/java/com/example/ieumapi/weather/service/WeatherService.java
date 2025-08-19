package com.example.ieumapi.weather.service;

import com.example.ieumapi.weather.client.WeatherApiClient;
import com.example.ieumapi.weather.dto.WeatherInfo;
import com.example.ieumapi.weather.dto.WeatherItem;
import com.example.ieumapi.weather.dto.WeatherResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WeatherService {

    @Value("${weather.api.key}")
    private String apiKey;

    private final WeatherApiClient weatherApiClient;

    public WeatherService(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    public WeatherInfo getShortTermForecast(String nx, String ny) {
        LocalDateTime now = LocalDateTime.now();

        // API 데이터가 보통 40-45분 사이에 생성되므로, 안정적인 조회를 위해 현재 시간이 45분 미만이면 1시간을 빼줍니다.
        if (now.getMinute() < 45) {
            now = now.minusHours(1);
        }

        String baseDate = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        // 초단기 실황 API는 정시(HH00)를 기준으로 데이터를 제공합니다.
        String baseTime = now.format(DateTimeFormatter.ofPattern("HH00"));

        WeatherResponse response = weatherApiClient.getShortTermForecast(apiKey, 1, 1000, "JSON", baseDate, baseTime, nx, ny);

        return extractWeatherInfo(response);
    }

    private WeatherInfo extractWeatherInfo(WeatherResponse response) {
        if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
            return WeatherInfo.builder().build(); // 빈 WeatherInfo 객체 반환
        }

        List<WeatherItem> items = response.getBody().getItems().getItem();
        String temperature = null;
        String precipitationProbability = null;
        String skyStatus = null;
        String precipitationType = "없음"; // 강수형태가 없는 경우 기본값

        for (WeatherItem item : items) {
            switch (item.getCategory()) {
                case "T1H": // 기온 (TA)
                    temperature = item.getFcstValue();
                    break;
                case "POP": // 강수확률 (ST)
                    precipitationProbability = item.getFcstValue();
                    break;
                case "SKY": // 하늘 상태
                    skyStatus = getSkyStatusString(item.getFcstValue());
                    break;
                case "PTY": // 강수 형태 (PREP)
                    precipitationType = getPrecipitationTypeString(item.getFcstValue());
                    break;
            }
        }

        return WeatherInfo.builder()
                .temperature(temperature)
                .precipitationProbability(precipitationProbability)
                .skyStatus(skyStatus)
                .precipitationType(precipitationType)
                .build();
    }

    private String getSkyStatusString(String skyCode) {
        if (skyCode == null) return "정보 없음";
        switch (skyCode) {
            case "1":
                return "맑음";
            case "2":
                return "구름조금";
            case "3":
                return "구름많음";
            case "4":
                return "흐림";
            default:
                return "정보 없음";
        }
    }

    private String getPrecipitationTypeString(String ptyCode) {
        if (ptyCode == null) return "없음";
        switch (ptyCode) {
            case "0":
                return "없음";
            case "1":
                return "비";
            case "2":
                return "비/눈";
            case "3":
                return "눈";
            case "4":
                return "눈/비";
            default:
                return "정보 없음";
        }
    }
}
