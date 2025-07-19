package com.devcamp.bery_real_estate.dtos;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class AddressMapDto {
    private int id;
    @NotEmpty(message = "Enter the address map")
    private String address;

    @NotNull(message = "Enter the latitude")
    private double latitude;

    @NotNull(message = "Enter the longitude")
    private double longitude;
}
