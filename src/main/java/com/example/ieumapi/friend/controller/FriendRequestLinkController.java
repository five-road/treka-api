package com.example.ieumapi.friend.controller;

import com.example.ieumapi.friend.dto.AcceptFriendLinkResponse;
import com.example.ieumapi.friend.dto.CreateFriendLinkRequest;
import com.example.ieumapi.friend.dto.DeclineFriendLinkResponse;
import com.example.ieumapi.friend.dto.FriendLinkInfoResponse;
import com.example.ieumapi.friend.service.FriendRequestLinkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend-requests/link")
public class FriendRequestLinkController {
    private final FriendRequestLinkService friendRequestLinkService;

    public FriendRequestLinkController(FriendRequestLinkService friendRequestLinkService) {
        this.friendRequestLinkService = friendRequestLinkService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FriendLinkInfoResponse createLink(
            @Valid @RequestBody CreateFriendLinkRequest request
    ) {
        return friendRequestLinkService.createLink(request);
    }

    @GetMapping("/{inviteCode}")
    public FriendLinkInfoResponse getLinkInfo(
            @PathVariable String inviteCode
    ) {
        return friendRequestLinkService.getLinkInfo(inviteCode);
    }

    @PostMapping("/{inviteCode}/accept")
    public AcceptFriendLinkResponse acceptLink(
            @PathVariable String inviteCode
    ) {
        return friendRequestLinkService.acceptLink(inviteCode);
    }

    @PostMapping("/{inviteCode}/decline")
    public DeclineFriendLinkResponse declineLink(
            @PathVariable String inviteCode
    ) {
        return friendRequestLinkService.declineLink(inviteCode);
    }
}
