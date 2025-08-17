package com.example.ieumapi.widy.dto;

import com.example.ieumapi.file.dto.UploadImageResDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WidyResponseDto {

    private final Long widyId;
    private final String title;
    private final String content;
    private final List<UploadImageResDto> images;

    @Builder
    public WidyResponseDto(Long widyId, String title, String content, List<UploadImageResDto> images) {
        this.widyId = widyId;
        this.title = title;
        this.content = content;
        this.images = images;
    }
}
