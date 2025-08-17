package com.example.ieumapi.widy.service;

import com.example.ieumapi.file.FileStorageClient;
import com.example.ieumapi.file.dto.UploadImageResDto;
import com.example.ieumapi.widy.domain.Widy;
import com.example.ieumapi.widy.domain.WidyImage;
import com.example.ieumapi.widy.dto.WidyCreateRequestDto;
import com.example.ieumapi.widy.dto.WidyResponseDto;
import com.example.ieumapi.widy.dto.WidyUpdateRequestDto;
import com.example.ieumapi.widy.repository.WidyImageRepository;
import com.example.ieumapi.widy.repository.WidyRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class WidyService {

    private final WidyRepository widyRepository;
    private final WidyImageRepository widyImageRepository;
    private final FileStorageClient fileStorageClient;

    public WidyResponseDto createWidy(WidyCreateRequestDto requestDto, List<MultipartFile> images) {
        // 1. Widy 엔티티 생성 및 저장
        Widy widy = requestDto.toEntity();
        Widy savedWidy = widyRepository.save(widy);
        Long widyId = savedWidy.getWidyId();

        // 2. 이미지 업로드 및 정보 저장
        List<UploadImageResDto> uploadedImages = fileStorageClient.uploadMultipleImages(images);

        List<WidyImage> widyImages = uploadedImages.stream()
            .map(imageDto -> WidyImage.builder()
                .widyId(widyId) // 저장된 Widy의 ID를 설정
                .id(imageDto.getId())
                .originalName(imageDto.getOriginalName())
                .storedName(imageDto.getStoredName())
                .url(imageDto.getUrl())
                .build())
            .collect(Collectors.toList());

        widyImageRepository.saveAll(widyImages);

        // 3. 응답 DTO 생성
        return WidyResponseDto.builder()
            .widyId(widyId)
            .title(savedWidy.getTitle())
            .content(savedWidy.getContent())
            .images(uploadedImages)
            .build();
    }

    @Transactional(readOnly = true)
    public WidyResponseDto getWidy(Long widyId) {
        Widy widy = widyRepository.findById(widyId)
            .orElseThrow(() -> new IllegalArgumentException("Widy not found"));

        List<WidyImage> widyImages = widyImageRepository.findByWidyId(widyId);

        List<UploadImageResDto> imageDtos = widyImages.stream()
            .map(image -> UploadImageResDto.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .storedName(image.getStoredName())
                .url(image.getUrl())
                .build())
            .collect(Collectors.toList());

        return WidyResponseDto.builder()
            .widyId(widy.getWidyId())
            .title(widy.getTitle())
            .content(widy.getContent())
            .images(imageDtos)
            .build();
    }

    @Transactional(readOnly = true)
    public List<WidyResponseDto> getByUserId(Long userId) {
        List<Widy> widys = widyRepository.findByUserId(userId);
        return widys.stream()
            .map(this::mapToWidyResponseDto)
            .collect(Collectors.toList());
    }

    public WidyResponseDto update(Long id, WidyUpdateRequestDto dto) {
        Widy widy = widyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Widy not found"));
        widy.update(dto.getTitle(), dto.getContent());
        return mapToWidyResponseDto(widy);
    }

    public void delete(Long id) {
        widyRepository.deleteById(id);
    }

    private WidyResponseDto mapToWidyResponseDto(Widy widy) {
        List<WidyImage> widyImages = widyImageRepository.findByWidyId(widy.getWidyId());
        List<UploadImageResDto> imageDtos = widyImages.stream()
            .map(image -> UploadImageResDto.builder()
                .id(image.getId())
                .originalName(image.getOriginalName())
                .storedName(image.getStoredName())
                .url(image.getUrl())
                .build())
            .collect(Collectors.toList());

        return WidyResponseDto.builder()
            .widyId(widy.getWidyId())
            .title(widy.getTitle())
            .content(widy.getContent())
            .images(imageDtos)
            .build();
    }
}
