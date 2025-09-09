package com.example.ieumapi.weather.service;

import com.example.ieumapi.weather.client.WeatherApiClient;
import com.example.ieumapi.weather.dto.WeatherItem;
import com.example.ieumapi.weather.dto.WeatherResponse;
import com.example.ieumapi.weather.dto.WeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final WeatherApiClient weatherApiClient;

    @Value("${api.kma.service-key}")
    private String serviceKey;

    public WeatherResponseDto getWeather() {
        LocalDateTime now = LocalDateTime.now();
        String baseDate = getBaseDate(now);
        String baseTime = getBaseTime(now);

        if ("2300".equals(baseTime)) {
            baseDate = getBaseDate(now.minusDays(1));
        }

        WeatherResponse weatherResponse = weatherApiClient.getVilageFcst(serviceKey, 1, 1000, "JSON",
                baseDate, baseTime, 55, 38);

        return extractWeatherInfo(weatherResponse);
    }

    private WeatherResponseDto extractWeatherInfo(WeatherResponse weatherResponse) {
        if (weatherResponse == null || weatherResponse.getResponse() == null || weatherResponse.getResponse().getBody() == null || weatherResponse.getResponse().getBody().getItems() == null || CollectionUtils.isEmpty(weatherResponse.getResponse().getBody().getItems().getItem())) {
            return new WeatherResponseDto(null, null, null);
        }

        List<WeatherItem> items = weatherResponse.getResponse().getBody().getItems().getItem();

        String temperature = items.stream()
                .filter(item -> "TMP".equals(item.getCategory()))
                .map(WeatherItem::getFcstValue)
                .findFirst()
                .orElse(null);

        String skyCondition = items.stream()
                .filter(item -> "SKY".equals(item.getCategory()))
                .map(WeatherItem::getFcstValue)
                .map(this::toSkyConditionString)
                .findFirst()
                .orElse(null);

        String precipitationProbability = items.stream()
                .filter(item -> "POP".equals(item.getCategory()))
                .map(WeatherItem::getFcstValue)
                .findFirst()
                .orElse(null);

        return new WeatherResponseDto(
                temperature,
                skyCondition,
                precipitationProbability
        );
    }

    private String toSkyConditionString(String skyCode) {
        if (skyCode == null) return null;
        return switch (skyCode) {
            case "1" -> "맑음";
            case "3" -> "구름많음";
            case "4" -> "흐림";
            default -> "알 수 없음";
        };
    }


    private String getBaseDate(LocalDateTime now) {
        return now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String getBaseTime(LocalDateTime now) {
        LocalTime nowTime = now.toLocalTime();
        int hour = nowTime.getHour();
        int minute = nowTime.getMinute();

        if (hour < 2 || (hour == 2 && minute <= 10)) {
            return "2300"; // 전날 23시
        } else if (hour < 5 || (hour == 5 && minute <= 10)) {
            return "0200";
        } else if (hour < 8 || (hour == 8 && minute <= 10)) {
            return "0500";
        } else if (hour < 11 || (hour == 11 && minute <= 10)) {
            return "0800";
        } else if (hour < 14 || (hour == 14 && minute <= 10)) {
            return "1100";
        } else if (hour < 17 || (hour == 17 && minute <= 10)) {
            return "1400";
        } else if (hour < 20 || (hour == 20 && minute <= 10)) {
            return "1700";
        } else if (hour < 23 || minute <= 10) {
            return "2000";
        } else {
            return "2300";
        }
    }
}
