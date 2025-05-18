package com.hastanerandevu.app.dto.Analytics;

import com.hastanerandevu.app.model.User;

public class UserGenderCountDTO {
    private User.Gender gender;
    private long count;

    public UserGenderCountDTO(User.Gender gender, long count) {
        this.gender = gender;
        this.count = count;
    }

    public User.Gender getGender() {
        return gender;
    }

    public long getCount() {
        return count;
    }
}
