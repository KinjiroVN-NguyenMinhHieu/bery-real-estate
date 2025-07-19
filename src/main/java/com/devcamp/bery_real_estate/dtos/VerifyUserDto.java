package com.devcamp.bery_real_estate.dtos;

import lombok.Data;

@Data
public class VerifyUserDto {
    private String username;
    private int realEstatesCount;

    public VerifyUserDto(String username, int realEstatesCount) {
        this.username = username;
        this.realEstatesCount = realEstatesCount;
    }

}
