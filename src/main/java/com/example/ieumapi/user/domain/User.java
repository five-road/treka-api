package com.example.ieumapi.user.domain;

import com.example.ieumapi.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickName;

    @Column(nullable = true)
    private String imageUrl;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private String snsType;

    @Column(nullable = false)
    private boolean isGuest = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean isActive = true;

    public void deactivate() {
        this.isActive = false;
    }
}
