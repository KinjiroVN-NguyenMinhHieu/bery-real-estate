package com.devcamp.bery_real_estate.dtos;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class RegionLinkDto {
    private int id;

    @NotEmpty(message = "Enter the region link name")
    private String name;
    private String description;
    private String photo;
    private String address;
    private double latitude;
    private double longitude;
}
