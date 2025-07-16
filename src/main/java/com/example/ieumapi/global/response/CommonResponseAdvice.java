package com.example.ieumapi.global.response;

import com.example.ieumapi.global.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class CommonResponseAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(CommonResponseAdvice.class);
    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
//        Class<?> responseType = returnType.getParameterType();
//
//        return !CommonResponse.class.isAssignableFrom(responseType)
//                && !ErrorResponse.class.isAssignableFrom(responseType);
//        boolean support = !ErrorResponse.class.isAssignableFrom(returnType.getParameterType());
//        log.info("[supports] {} => {}", returnType.getMethod(), support);
//        return support;
        return !ErrorResponse.class.isAssignableFrom(returnType.getParameterType());
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  org.springframework.http.server.ServerHttpRequest request,
                                  org.springframework.http.server.ServerHttpResponse response) {

        if (body instanceof ErrorResponse) {
            return body;
        }

        String message = "요청이 성공적으로 처리되었습니다.";

        if (request instanceof ServletServerHttpRequest servletReq) {
            Object handler = servletReq.getServletRequest()
                    .getAttribute(HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE);
            if (handler instanceof HandlerMethod hm) {
                Method m = hm.getMethod();
                SuccessMessage anno = m.getAnnotation(SuccessMessage.class);
                if (anno != null) {
                    message = anno.value();
                }
            }
        }

        if (body instanceof CommonResponse<?> existing) {
            return CommonResponse.<Object>builder()
                    .success(existing.isSuccess())
                    .data(existing.getData())
                    .message(message)
                    .build();
        }

        return CommonResponse.builder()
                .success(true)
                .data(body)
                .message(message)
                .build();
    }
}
