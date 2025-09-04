package com.example.ieumapi.localplace.controller;

import com.example.ieumapi.localplace.dto.LocalPlaceCreateRequest;
import com.example.ieumapi.localplace.dto.LocalPlaceResponse;
import com.example.ieumapi.localplace.dto.LocalPlaceSearchResponse;
import com.example.ieumapi.localplace.dto.LocalPlaceUpdateRequest;
import com.example.ieumapi.localplace.service.LocalPlaceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/local-places")
@RequiredArgsConstructor
public class LocalPlaceController {

    private final LocalPlaceService localPlaceService;

    @Operation(summary = "키워드로 장소 검색 (DB + TOUR API)")
    @GetMapping("/search")
    public ResponseEntity<List<LocalPlaceSearchResponse>> searchPlaces(@RequestParam("keyword") String keyword) {
        return ResponseEntity.ok(localPlaceService.searchLocalPlaces(keyword));
    }

    @Operation(summary = "로컬 장소 생성")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LocalPlaceResponse> createPlace(@RequestPart("request") LocalPlaceCreateRequest request,
                                                          @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(localPlaceService.createPlace(request, images));
    }

    @Operation(summary = "특정 로컬 장소 조회")
    @GetMapping("/{id}")
    public ResponseEntity<LocalPlaceResponse> getPlace(@PathVariable("id") Long placeId) {
        return ResponseEntity.ok(localPlaceService.getPlace(placeId));
    }

    @Operation(summary = "로컬 장소 정보 수정")
    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<LocalPlaceResponse> updatePlace(@PathVariable("id") Long placeId,
                                                          @RequestPart("request") LocalPlaceUpdateRequest request,
                                                          @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return ResponseEntity.ok(localPlaceService.updatePlace(placeId, request, images));
    }

    @Operation(summary = "로컬 장소 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable("id") Long placeId) {
        localPlaceService.deletePlace(placeId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "좌표 기반 근처 장소 조회")
    @GetMapping("/nearby")
    public ResponseEntity<List<LocalPlaceSearchResponse>> findNearbyPlaces(@RequestParam("lat") double latitude, @RequestParam("lng") double longitude) {
        return ResponseEntity.ok(localPlaceService.findNearbyPlaces(latitude, longitude));
    }
}
