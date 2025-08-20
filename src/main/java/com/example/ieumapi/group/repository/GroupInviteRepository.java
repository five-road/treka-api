package com.example.ieumapi.group.repository;

import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.group.domain.GroupInvite;
import com.example.ieumapi.group.domain.InviteStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    List<GroupInvite> findByGroup(Group group);
    Optional<GroupInvite> findByInviteCode(String inviteCode);
    Optional<GroupInvite> findByInviteIdAndGroupGroupId(Long inviteId, Long groupId);

    void deleteByInviteIdAndGroupGroupId(Long inviteId, Long groupId);

    @Query("""
        SELECT gi
        FROM GroupInvite gi
        WHERE gi.group = :group
          AND gi.toUser IS NULL
          AND gi.status = com.example.ieumapi.group.domain.InviteStatus.PENDING
          AND (gi.expiresAt IS NULL OR gi.expiresAt > CURRENT_TIMESTAMP)
        ORDER BY gi.createdAt DESC
        """)
    Optional<GroupInvite> findActiveLinkInvite(@Param("group") Group group);
}