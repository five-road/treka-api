package com.example.ieumapi.localplace.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kto-api", url ="https://apis.data.go.kr/B551011/KorService2")
public interface KtoFeignClient {

    @GetMapping("/searchKeyword2")
    String searchByKeyword(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("keyword") String keyword,
            @RequestParam("_type") String type,
            @RequestParam("MobileOS") String mobileOS,
            @RequestParam("MobileApp") String mobileApp,
            @RequestParam("areaCode") String areaCode
    );

    @GetMapping("/locationBasedList2")
    String searchByLocation(
            @RequestParam("serviceKey") String serviceKey,
            @RequestParam("mapX") double mapX,
            @RequestParam("mapY") double mapY,
            @RequestParam("radius") int radius,
            @RequestParam("_type") String type,
            @RequestParam("MobileOS") String mobileOS,
            @RequestParam("MobileApp") String mobileApp
    );
}
