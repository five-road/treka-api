package com.example.ieumapi.weather.dto;

import lombok.Data;

@Data
public class WeatherItem {
    private String baseDate;
    private String baseTime;
    private String category;
    private String fcstDate;
    private String fcstTime;
    private String fcstValue;
    private int nx;
    private int ny;
}
