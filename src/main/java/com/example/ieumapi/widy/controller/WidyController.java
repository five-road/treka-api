package com.example.ieumapi.widy.controller;

import com.example.ieumapi.widy.dto.*;
import com.example.ieumapi.widy.service.WidyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/widy")
@RequiredArgsConstructor
public class WidyController {
    private final WidyService widyService;

    @PostMapping
    public ResponseEntity<WidyResponseDto> create(@RequestBody WidyCreateRequestDto dto) {
        return ResponseEntity.ok(widyService.create(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WidyResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(widyService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<WidyResponseDto>> getByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok(widyService.getByUserId(userId));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<WidyResponseDto> update(@PathVariable Long id, @RequestBody WidyUpdateRequestDto dto) {
        return ResponseEntity.ok(widyService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        widyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

