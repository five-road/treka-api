package com.example.ieumapi.weather.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import lombok.Data;

@Data
public class WeatherItem {
    @JacksonXmlProperty(localName = "baseDate")
    private String baseDate;
    @JacksonXmlProperty(localName = "baseTime")
    private String baseTime;
    @JacksonXmlProperty(localName = "category")
    private String category;
    @JacksonXmlProperty(localName = "fcstDate")
    private String fcstDate;
    @JacksonXmlProperty(localName = "fcstTime")
    private String fcstTime;
    @JacksonXmlProperty(localName = "fcstValue")
    private String fcstValue;
    @JacksonXmlProperty(localName = "nx")
    private int nx;
    @JacksonXmlProperty(localName = "ny")
    private int ny;
}
