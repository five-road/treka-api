package com.example.ieumapi.widy.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WidyImage extends BaseEntity {
    @Id
    private String id;
    private String originalName;
    private String storedName;
    private String url;
    private Long widyId;

    @Builder
    public WidyImage(String id, String originalName, String storedName, String url, Long widyId) {
        this.id = id;
        this.originalName = originalName;
        this.storedName = storedName;
        this.url = url;
        this.widyId = widyId;
    }
}
