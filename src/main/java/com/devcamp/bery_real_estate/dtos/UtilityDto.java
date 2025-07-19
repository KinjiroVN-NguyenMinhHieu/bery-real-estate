package com.devcamp.bery_real_estate.dtos;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class UtilityDto {
    private int id;

    @NotEmpty(message = "Enter the utility name")
    private String name;
    private String description;
    private String photo;
}
