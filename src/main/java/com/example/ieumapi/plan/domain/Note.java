package com.example.ieumapi.plan.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "note")
public class Note extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noteId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(nullable = false, length = 4000)
    private String content;

    @Column
    private LocalDate date; // 메모에 날짜를 붙일 수 있음(옵션)

    @Column(nullable = false)
    private boolean pinned; // 고정 여부

    public void update(String content, LocalDate date, Boolean pinned) {
        if (content != null) this.content = content;
        if (date != null) this.date = date;
        if (pinned != null) this.pinned = pinned;
    }
}
