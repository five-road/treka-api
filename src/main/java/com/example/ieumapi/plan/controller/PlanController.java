package com.example.ieumapi.plan.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.plan.dto.plan.CreatePlanRequest;
import com.example.ieumapi.plan.dto.plan.PlanDto;
import com.example.ieumapi.plan.dto.plan.UpdatePlanRequest;
import com.example.ieumapi.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plan", description = "여행 계획 API")
@RestController
@RequestMapping("/api/v1/plans")
@Validated
@RequiredArgsConstructor
public class PlanController {

    private final PlanService planService;

    @Operation(summary = "플랜 생성", description = "개인/그룹 플랜 생성. groupId를 지정하면 해당 그룹의 플랜으로 생성됩니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SuccessMessage("플랜을 생성했습니다.")
    public PlanDto create(@Valid @RequestBody CreatePlanRequest request) {
        return planService.create(request);
    }

    @Operation(summary = "내 개인 플랜 목록(커서)", description = "Base64 커서 기반, 제목 검색(q) 가능")
    @GetMapping
    @SuccessMessage("플랜 목록을 불러왔습니다.")
    public CursorPageResponse<PlanDto> listMyPlans(
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, name = "q") String query
    ) {
        return planService.listMyPlans(size, cursor, query);
    }

    @Operation(summary = "플랜 상세", description = "플랜 정보를 조회합니다.")
    @GetMapping("/{planId}")
    @SuccessMessage("플랜 상세를 불러왔습니다.")
    public PlanDto get(@PathVariable Long planId) {
        return planService.get(planId);
    }

    @Operation(summary = "플랜 수정", description = "플랜 제목/기간/설명/지역을 수정합니다.")
    @PutMapping("/{planId}")
    @SuccessMessage("플랜을 수정했습니다.")
    public PlanDto update(@PathVariable Long planId, @Valid @RequestBody UpdatePlanRequest request) {
        return planService.update(planId, request);
    }

    @Operation(summary = "플랜 삭제", description = "플랜을 삭제합니다.")
    @DeleteMapping("/{planId}")
    @SuccessMessage("플랜을 삭제했습니다.")
    public CommonResponse<Void> delete(@PathVariable Long planId) {
        planService.delete(planId);
        return CommonResponse.success();
    }
}
