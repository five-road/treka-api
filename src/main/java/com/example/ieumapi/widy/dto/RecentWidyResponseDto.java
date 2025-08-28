package com.example.ieumapi.widy.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecentWidyResponseDto {

    private final Long widyId;
    private final String title;
    private final String planName;
    private final String groupName;

}
