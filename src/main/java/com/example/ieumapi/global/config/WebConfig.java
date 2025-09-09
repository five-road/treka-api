package com.example.ieumapi.global.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MultipartJsonMessageConverter multipartJsonMessageConverter;


    public WebConfig(MultipartJsonMessageConverter multipartJsonMessageConverter) {
        this.multipartJsonMessageConverter = multipartJsonMessageConverter;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(multipartJsonMessageConverter);
    }
}
