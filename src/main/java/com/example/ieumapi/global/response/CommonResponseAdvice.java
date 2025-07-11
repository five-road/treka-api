package com.example.ieumapi.global.response;

import com.example.ieumapi.global.exception.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice
public class CommonResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType,
                            Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> responseType = returnType.getParameterType();
        return !CommonResponse.class.isAssignableFrom(responseType)
                && !ErrorResponse.class.isAssignableFrom(responseType);
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

        SuccessMessage anno = returnType.getMethodAnnotation(SuccessMessage.class);
        String message = (anno != null)
                ? anno.value()
                : "요청이 성공적으로 처리되었습니다.";

        return CommonResponse.builder()
                .success(true)
                .data(body)
                .message(message)
                .build();
    }
}
