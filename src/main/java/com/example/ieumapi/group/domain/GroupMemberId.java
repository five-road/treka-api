package com.example.ieumapi.group.domain;

import java.util.Objects;

public class GroupMemberId {
    private Long groupId;
    private Long userId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(!(o instanceof GroupMemberId)) return false;
        GroupMemberId that = (GroupMemberId) o;
        return Objects.equals(groupId, that.groupId) &&
                Objects.equals(userId, that.userId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(groupId, userId);
    }
}
