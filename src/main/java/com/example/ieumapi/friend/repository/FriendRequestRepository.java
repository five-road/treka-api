package com.example.ieumapi.friend.repository;

import com.example.ieumapi.friend.domain.FriendRequest;
import com.example.ieumapi.friend.domain.RequestStatus;
import com.example.ieumapi.user.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    boolean existsByFromUserAndToUserAndStatus(
            User fromUser, User toUser, RequestStatus status
    );
    List<FriendRequest> findByToUserAndStatusAndRequestIdLessThanOrderByRequestIdDesc(
            User toUser,
            RequestStatus status,
            Long cursorId,
            Pageable pageable
    );
    List<FriendRequest> findByFromUserAndRequestIdLessThanOrderByRequestIdDesc(
            User fromUser,
            Long cursorId,
            Pageable pageable
    );
}
