package com.example.ieumapi.weather.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.Data;

import java.util.List;

@Data
@JacksonXmlRootElement(localName = "response")
public class WeatherResponse {
    private Header header;
    private Body body;

    @Data
    public static class Header {
        @JacksonXmlProperty(localName = "resultCode")
        private String resultCode;
        @JacksonXmlProperty(localName = "resultMsg")
        private String resultMsg;
    }

    @Data
    public static class Body {
        @JacksonXmlProperty(localName = "dataType")
        private String dataType;
        @JacksonXmlProperty(localName = "items")
        private Items items;
        @JacksonXmlProperty(localName = "pageNo")
        private int pageNo;
        @JacksonXmlProperty(localName = "numOfRows")
        private int numOfRows;
        @JacksonXmlProperty(localName = "totalCount")
        private int totalCount;
    }

    @Data
    public static class Items {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "item")
        private List<WeatherItem> item;
    }
}
