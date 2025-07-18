package com.example.ieumapi.friend.repository;

import com.example.ieumapi.friend.domain.Friend;
import com.example.ieumapi.friend.domain.FriendId;
import com.example.ieumapi.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Friend, FriendId> {
    boolean existsByUserAndFriend(User user, User friend);

    List<Friend> findByUserAndCreatedAtLessThan(
            User user,
            LocalDateTime cursorTime,
            Pageable pageable
    );

    void deleteByUserIdAndFriendId(Long userId, Long friendId);
}