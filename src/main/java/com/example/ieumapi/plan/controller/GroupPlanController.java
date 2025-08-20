package com.example.ieumapi.plan.controller;

import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.plan.dto.plan.PlanDto;
import com.example.ieumapi.plan.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Plan (Group)", description = "그룹의 플랜 목록 API")
@RestController
@RequestMapping("/api/v1/groups/{groupId}/plans")
@Validated
@RequiredArgsConstructor
public class GroupPlanController {

    private final PlanService planService;

    @Operation(summary = "그룹 플랜 목록(커서)", description = "Base64 커서 기반, 제목 검색(q) 가능")
    @GetMapping
    @SuccessMessage("그룹 플랜 목록을 불러왔습니다.")
    public CursorPageResponse<PlanDto> listGroupPlans(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false, name = "q") String query
    ) {
        return planService.listGroupPlans(groupId, size, cursor, query);
    }
}
