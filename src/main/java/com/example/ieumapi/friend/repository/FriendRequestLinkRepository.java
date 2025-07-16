package com.example.ieumapi.friend.repository;

import com.example.ieumapi.friend.domain.FriendRequestLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FriendRequestLinkRepository extends JpaRepository<FriendRequestLink, Long> {
    Optional<FriendRequestLink> findByInviteCode(String inviteCode);
}