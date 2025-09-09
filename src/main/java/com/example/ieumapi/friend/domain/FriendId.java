package com.example.ieumapi.friend.domain;

import java.io.Serializable;
import java.util.Objects;

public class FriendId implements Serializable {
    private Long userId;
    private Long friendId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FriendId)) return false;
        FriendId that = (FriendId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(friendId, that.friendId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
