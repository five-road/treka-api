package com.example.ieumapi.plan.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "plan")
public class Plan extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long planId;

    @Column(nullable = false, length = 100)
    private String title;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group; // null이면 개인 플랜

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    @Column(length = 120)
    private String locationName; // 도시/지역명(옵션)

    public void update(String title, String description, LocalDate startDate, LocalDate endDate, String locationName) {
        if (title != null) this.title = title;
        if (description != null) this.description = description;
        if (startDate != null) this.startDate = startDate;
        if (endDate != null) this.endDate = endDate;
        if (locationName != null) this.locationName = locationName;
    }

}
