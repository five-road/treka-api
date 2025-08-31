package com.example.ieumapi.widy.dto;

import com.example.ieumapi.file.dto.UploadImageResDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class WidyResponseDto {
    private final String email;
    private final Long widyId;
    private final String title;
    private final String content;
    private final String address;
    private final List<UploadImageResDto> images;


    @Builder
    public WidyResponseDto(String email, Long widyId, String title, String content, String address, List<UploadImageResDto> images) {
        this.email = email;
        this.widyId = widyId;
        this.title = title;
        this.content = content;
        this.address = address;
        this.images = images;
    }
}
