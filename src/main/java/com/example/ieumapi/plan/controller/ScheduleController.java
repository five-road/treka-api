package com.example.ieumapi.plan.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.plan.dto.schedule.CreateScheduleRequest;
import com.example.ieumapi.plan.dto.schedule.ScheduleDto;
import com.example.ieumapi.plan.dto.schedule.UpdateScheduleRequest;
import com.example.ieumapi.plan.service.ScheduleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Schedule", description = "여행 계획 내 일정 API")
@RestController
@RequestMapping("/api/v1/plans/{planId}/schedules")
@Validated
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // POST /api/v1/plans/{planId}/schedules
    @Operation(summary = "일정 생성", description = "여행 계획에 일정을 추가합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SuccessMessage("일정을 생성했습니다.")
    public ScheduleDto create(
            @PathVariable Long planId,
            @Valid @RequestBody CreateScheduleRequest request
    ) {
        return scheduleService.create(planId, request);
    }

    // GET /api/v1/plans/{planId}/schedules?date=YYYY-MM-DD&size=20&cursor=...
    @Operation(summary = "일정 목록 조회(커서)", description = "Base64 커서 기반 페이징. date가 있으면 해당 날짜의 일정만 조회합니다.")
    @GetMapping
    @SuccessMessage("일정 목록을 불러왔습니다.")
    public CursorPageResponse<ScheduleDto> list(
            @PathVariable Long planId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false) String cursor
    ) {
        return scheduleService.list(planId, date, size, cursor);
    }

    // GET /api/v1/plans/{planId}/schedules/{scheduleId}
    @Operation(summary = "일정 단건 조회", description = "ID로 일정 상세를 가져옵니다.")
    @GetMapping("/{scheduleId}")
    @SuccessMessage("일정 상세를 불러왔습니다.")
    public ScheduleDto getOne(
            @PathVariable Long planId,
            @PathVariable Long scheduleId
    ) {
        return scheduleService.get(planId, scheduleId);
    }

    // PUT /api/v1/plans/{planId}/schedules/{scheduleId}
    @Operation(summary = "일정 수정", description = "일정 정보를 수정합니다. 부분 수정 허용.")
    @PutMapping("/{scheduleId}")
    @SuccessMessage("일정을 수정했습니다.")
    public ScheduleDto update(
            @PathVariable Long planId,
            @PathVariable Long scheduleId,
            @Valid @RequestBody UpdateScheduleRequest request
    ) {
        return scheduleService.update(planId, scheduleId, request);
    }

    // DELETE /api/v1/plans/{planId}/schedules/{scheduleId}
    @Operation(summary = "일정 삭제", description = "일정을 삭제합니다.")
    @DeleteMapping("/{scheduleId}")
    @SuccessMessage("일정을 삭제했습니다.")
    public CommonResponse<Void> delete(
            @PathVariable Long planId,
            @PathVariable Long scheduleId
    ) {
        scheduleService.delete(planId, scheduleId);
        return CommonResponse.success();
    }
}
