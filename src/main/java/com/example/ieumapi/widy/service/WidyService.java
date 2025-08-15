package com.example.ieumapi.widy.service;

import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.dto.WidyCreateRequestDto;
import com.example.ieumapi.widy.dto.WidyUpdateRequestDto;
import com.example.ieumapi.widy.dto.WidyResponseDto;
import com.example.ieumapi.widy.repository.WidyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WidyService {
    private final WidyRepository widyRepository;

    @Transactional
    public WidyResponseDto create(WidyCreateRequestDto dto) {
        Widy widy = Widy.builder()
                .userId(dto.getUserId())
                .content(dto.getContent())
                .photoId(dto.getPhotoId())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        widyRepository.save(widy);
        return toDto(widy);
    }

    @Transactional(readOnly = true)
    public WidyResponseDto getById(Long id) {
        Widy widy = widyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Widy not found"));
        return toDto(widy);
    }

    @Transactional(readOnly = true)
    public List<WidyResponseDto> getByUserId(Long userId) {
        return widyRepository.findAllByUserId(userId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Transactional
    public WidyResponseDto update(Long id, WidyUpdateRequestDto dto) {
        Widy widy = widyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Widy not found"));
        widy.setContent(dto.getContent());
        widy.setPhotoId(dto.getPhotoId());
        widy.setUpdatedAt(LocalDateTime.now());
        return toDto(widy);
    }

    @Transactional
    public void delete(Long id) {
        widyRepository.deleteById(id);
    }

    private WidyResponseDto toDto(Widy widy) {
        return WidyResponseDto.builder()
                .id(widy.getId())
                .userId(widy.getUserId())
                .content(widy.getContent())
                .photoId(widy.getPhotoId())
                .createdAt(widy.getCreatedAt())
                .updatedAt(widy.getUpdatedAt())
                .build();
    }
}

