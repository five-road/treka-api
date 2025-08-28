package com.example.ieumapi.widy.controller;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.widy.dto.RecentWidyResponseDto;
import com.example.ieumapi.widy.dto.WidyCountResponseDto;
import com.example.ieumapi.widy.dto.WidyCreateRequestDto;
import com.example.ieumapi.widy.dto.WidyResponseDto;
import com.example.ieumapi.widy.dto.WidyUpdateRequestDto;
import com.example.ieumapi.widy.service.WidyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Widy", description = "Widy 관련 API")
@RestController
@RequestMapping("/api/v1/widy")
@RequiredArgsConstructor
public class WidyController {
    private final WidyService widyService;

    @Operation(summary = "Widy 생성", description = "새로운 Widy를 생성합니다.")
    @PostMapping()
    public ResponseEntity<WidyResponseDto> create(@RequestPart("widy") @Valid WidyCreateRequestDto request
    , @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        WidyResponseDto widyResponseDto = widyService.createWidy(request, images);
        return ResponseEntity.ok(widyResponseDto);
    }

    @Operation(summary = "Widy 단건 조회", description = "ID를 통해 특정 Widy를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<WidyResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(widyService.getWidy(id));
    }

    @Operation(summary = "내 Widy 목록 조회", description = "현재 로그인한 사용자가 작성한 모든 Widy를 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<CursorPageResponse<WidyResponseDto>> getMyWidys(
        @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
        @RequestParam(required = false) String cursor
    ) {
        return ResponseEntity.ok(widyService.getMyWidysCursor(size, cursor));
    }

    @Operation(summary = "Widy 수정", description = "기존 Widy를 수정합니다. 본인만 수정할 수 있습니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<WidyResponseDto> update(
        @PathVariable Long id,
        @RequestBody @Valid WidyUpdateRequestDto dto) {
        return ResponseEntity.ok(widyService.update(id, dto));
    }

    @Operation(summary = "Widy 삭제", description = "특정 Widy를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        widyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "공개 Widy 조회", description = "공개된 모든 Widy를 최신순으로 조회합니다.")
    @GetMapping("/public")
    public ResponseEntity<CursorPageResponse<WidyResponseDto>> getPublicWidys(
        @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
        @RequestParam(required = false) String cursor
    ) {
        return ResponseEntity.ok(widyService.getPublicWidysCursor(size, cursor));
    }

    @Operation(summary = "그룹 피드 조회", description = "사용자가 속한 그룹의 모든 Widy를 최신순으로 조회합니다.")
    @GetMapping("/feed")
    public ResponseEntity<CursorPageResponse<WidyResponseDto>> getGroupFeed(
        @RequestParam(defaultValue = "10") @Min(1) @Max(50) int size,
        @RequestParam(required = false) String cursor
    ) {
        return ResponseEntity.ok(widyService.getGroupFeedCursor(size, cursor));
    }

    @Operation(summary = "나와 관련된 위디 총 개수 조회")
    @GetMapping("/count")
    public ResponseEntity<WidyCountResponseDto> getVisibleWidyCount() {
        return ResponseEntity.ok(widyService.getVisibleWidyCount());
    }

    @Operation(summary = "나와 관련된 최근 위디 조회", description = "최근 한 달 동안 나와 관련된 위디를 최대 7개까지 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공", content = @Content(schema = @Schema(implementation = RecentWidyResponseDto.class)))
    @GetMapping("/recent")
    public ResponseEntity<List<RecentWidyResponseDto>> getRecentVisibleWidys() {
        return ResponseEntity.ok(widyService.getRecentVisibleWidys());
    }
}
