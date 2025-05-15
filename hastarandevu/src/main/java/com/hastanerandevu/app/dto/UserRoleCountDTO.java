package com.hastanerandevu.app.dto;

import com.hastanerandevu.app.model.User;

public class UserRoleCountDTO {
    private User.Role role;
    private long count;

    public UserRoleCountDTO(User.Role role, long count) {
        this.role = role;
        this.count = count;
    }

    public User.Role getRole() {
        return role;
    }

    public long getCount() {
        return count;
    }
}
