package com.devcamp.bery_real_estate.dtos;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class DesignUnitDto {
    private int id;

    @NotEmpty(message = "Enter the Design Unit name")
    private String name;
    private String description;
    private int addressId;
    private String addressAddress;
    private String phone;
    private String phone2;
    private String fax;

    @Email
    private String email;
    private String website;
    private String note;
}
