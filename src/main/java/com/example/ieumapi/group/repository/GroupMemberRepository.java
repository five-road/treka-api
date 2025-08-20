package com.example.ieumapi.group.repository;

import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.group.domain.GroupMember;
import com.example.ieumapi.group.domain.GroupMemberId;
import com.example.ieumapi.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, GroupMemberId> {

    boolean existsByGroupGroupIdAndUserId(Long groupId, Long userId);

    List<GroupMember> findByGroup(Group group);

    List<GroupMember> findByUserAndJoinedAtLessThanOrderByJoinedAtDesc(
            User user,
            LocalDateTime cursorTime,
            Pageable pageable
    );

    Optional<GroupMember> findByGroupGroupIdAndUserId(Long groupId, Long userId);

    void deleteByGroupGroupIdAndUserId(Long groupId, Long userId);

    long countByGroupGroupId(Long groupId);
}
