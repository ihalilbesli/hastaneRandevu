package com.hastanerandevu.app.dto;

import com.hastanerandevu.app.model.User;

public class UserBloodTypeCountDTO {
    private User.Bloodtype bloodType;
    private long count;

    public UserBloodTypeCountDTO(User.Bloodtype bloodType, long count) {
        this.bloodType = bloodType;
        this.count = count;
    }

    public User.Bloodtype getBloodType() {
        return bloodType;
    }

    public long getCount() {
        return count;
    }
}
