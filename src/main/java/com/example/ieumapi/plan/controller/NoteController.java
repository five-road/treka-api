package com.example.ieumapi.plan.controller;

import com.example.ieumapi.global.response.CommonResponse;
import com.example.ieumapi.global.response.CursorPageResponse;
import com.example.ieumapi.global.response.SuccessMessage;
import com.example.ieumapi.plan.dto.note.CreateNoteRequest;
import com.example.ieumapi.plan.dto.note.NoteDto;
import com.example.ieumapi.plan.dto.note.UpdateNoteRequest;
import com.example.ieumapi.plan.service.NoteService;
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

@Tag(name = "Note", description = "여행 계획 내 메모 API")
@RestController
@RequestMapping("/api/v1/plans/{planId}/notes")
@Validated
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // POST /api/v1/plans/{planId}/notes
    @Operation(summary = "메모 생성", description = "여행 계획에 메모를 추가합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @SuccessMessage("메모를 생성했습니다.")
    public NoteDto create(
            @PathVariable Long planId,
            @Valid @RequestBody CreateNoteRequest request
    ) {
        return noteService.create(planId, request);
    }

    // GET /api/v1/plans/{planId}/notes?size=20&cursor=...&date=YYYY-MM-DD&pinnedOnly=false
    @Operation(summary = "메모 목록 조회(커서)", description = "Base64 커서 기반 페이징. pinnedOnly가 true면 고정 메모만 조회합니다.")
    @GetMapping
    @SuccessMessage("메모 목록을 불러왔습니다.")
    public CursorPageResponse<NoteDto> list(
            @PathVariable Long planId,
            @RequestParam(required = false) LocalDate date,
            @RequestParam(defaultValue = "false") boolean pinnedOnly,
            @RequestParam(defaultValue = "20")
            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하이어야 합니다.")
            int size,
            @RequestParam(required = false) String cursor
    ) {
        return noteService.list(planId, date, pinnedOnly, size, cursor);
    }

    // GET /api/v1/plans/{planId}/notes/{noteId}
    @Operation(summary = "메모 단건 조회", description = "ID로 메모 상세를 가져옵니다.")
    @GetMapping("/{noteId}")
    @SuccessMessage("메모 상세를 불러왔습니다.")
    public NoteDto getOne(
            @PathVariable Long planId,
            @PathVariable Long noteId
    ) {
        return noteService.get(planId, noteId);
    }

    // PUT /api/v1/plans/{planId}/notes/{noteId}
    @Operation(summary = "메모 수정", description = "메모 내용을 수정합니다.")
    @PutMapping("/{noteId}")
    @SuccessMessage("메모를 수정했습니다.")
    public NoteDto update(
            @PathVariable Long planId,
            @PathVariable Long noteId,
            @Valid @RequestBody UpdateNoteRequest request
    ) {
        return noteService.update(planId, noteId, request);
    }

    // DELETE /api/v1/plans/{planId}/notes/{noteId}
    @Operation(summary = "메모 삭제", description = "메모를 삭제합니다.")
    @DeleteMapping("/{noteId}")
    @SuccessMessage("메모를 삭제했습니다.")
    public CommonResponse<Void> delete(
            @PathVariable Long planId,
            @PathVariable Long noteId
    ) {
        noteService.delete(planId, noteId);
        return CommonResponse.success();
    }
}
