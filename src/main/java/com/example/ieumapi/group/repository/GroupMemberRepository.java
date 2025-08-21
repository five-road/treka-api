package com.example.ieumapi.group.repository;

import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.group.domain.GroupMember;
import com.example.ieumapi.group.domain.GroupMemberId;
import com.example.ieumapi.user.domain.User;
import feign.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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

    /**
     * 특정 사용자가 속한 모든 그룹의 ID를 가입일 내림차순으로 조회합니다.
     * @param user 조회할 사용자
     * @return 그룹 ID 리스트
     */
    @Query("SELECT gm.groupId FROM GroupMember gm WHERE gm.user = :user ORDER BY gm.joinedAt DESC")
    List<Long> findGroupIdsByUserOrderByJoinedAtDesc(@Param("user") User user);
}
