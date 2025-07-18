package com.example.ieumapi.group.repository;

import com.example.ieumapi.group.domain.Group;
import com.example.ieumapi.group.domain.GroupInvite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupInviteRepository extends JpaRepository<GroupInvite, Long> {
    List<GroupInvite> findByGroup(Group group);
    Optional<GroupInvite> findByInviteCode(String inviteCode);
    Optional<GroupInvite> findByInviteIdAndGroupGroupId(Long inviteId, Long groupId);

    void deleteByInviteIdAndGroupGroupId(Long inviteId, Long groupId);
}