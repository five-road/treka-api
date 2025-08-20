package com.example.ieumapi.plan.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.plan.dto.place.CreatePlaceRequest;
import com.example.ieumapi.plan.dto.place.PlaceDto;
import com.example.ieumapi.plan.dto.place.UpdatePlaceRequest;
import com.example.ieumapi.plan.service.PlaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Place", description = "여행 계획 내 위치 API")
@RestController
@RequestMapping("/api/v1/plans/{planId}/places")
@Validated
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    @Operation(summary = "장소 생성", description = "여행 계획에 위치를 추가합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SuccessMessage("위치를 저장했습니다.")
    public PlaceDto create(
            @PathVariable Long planId,
            @Valid @RequestBody CreatePlaceRequest request
    ) {
        return placeService.create(planId, request);
    }

    @Operation(summary = "장소 목록 조회(커서)", description = "query로 이름 검색, Base64 커서 기반 페이징")
    @GetMapping
    @SuccessMessage("위치 목록을 불러왔습니다.")
    public CursorPageResponse<PlaceDto> list(
            @PathVariable Long planId,
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false) String cursor
    ) {
        return placeService.list(planId, query, size, cursor);
    }

    @Operation(summary = "장소 단건 조회", description = "ID로 장소 상세를 가져옵니다.")
    @GetMapping("/{placeId}")
    @SuccessMessage("위치 상세를 불러왔습니다.")
    public PlaceDto getOne(
            @PathVariable Long planId,
            @PathVariable Long placeId
    ) {
        return placeService.get(planId, placeId);
    }

    @Operation(summary = "장소 수정", description = "장소 정보를 수정합니다.")
    @PutMapping("/{placeId}")
    @SuccessMessage("위치를 수정했습니다.")
    public PlaceDto update(
            @PathVariable Long planId,
            @PathVariable Long placeId,
            @Valid @RequestBody UpdatePlaceRequest request
    ) {
        return placeService.update(planId, placeId, request);
    }

    @Operation(summary = "장소 삭제", description = "장소를 삭제합니다.")
    @DeleteMapping("/{placeId}")
    @SuccessMessage("위치를 삭제했습니다.")
    public CommonResponse<Void> delete(
            @PathVariable Long planId,
            @PathVariable Long placeId
    ) {
        placeService.delete(planId, placeId);
        return CommonResponse.success();
    }
}
