package com.example.ieumapi.group.domain;

import com.example.ieumapi.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "`group`")
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(length = 255) // nullable 허용
    private String description;

    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<GroupMember> members = new ArrayList<>();

    @OneToMany(
            mappedBy = "group",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true
    )
    private List<GroupInvite> invites = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description){this.description = description;}
}
