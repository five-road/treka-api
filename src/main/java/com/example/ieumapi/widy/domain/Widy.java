package com.example.ieumapi.widy.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDate;

import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Widy extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long widyId;
    private Long scheduleId;
    private Long groupId;
    private String title;
    private String content;
    private Long userId;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private WidyScope scope;

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(
        name = "widy_emotions", // 1. 원하는 테이블 이름을 지정합니다.
        joinColumns = @JoinColumn(name = "widy_id") // 2. Widy 테이블을 참조할 외래 키 컬럼 이름을 지정합니다.
    )
    private Set<WidyEmotion> widyEmotionList;



    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}
