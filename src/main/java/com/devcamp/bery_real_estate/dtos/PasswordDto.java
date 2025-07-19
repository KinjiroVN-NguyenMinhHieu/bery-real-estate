package com.devcamp.bery_real_estate.dtos;

import lombok.Data;

@Data
public class PasswordDto {
    private String oldPassword;
    private String newPassword;
}
