package com.devcamp.bery_real_estate.dtos;

import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class MasterLayoutDto {
    private int id;

    @NotEmpty(message = "Enter the master layout name")
    private String name;
    private String description;
    private int projectId;
    private String projectName;
    private BigDecimal acreage;
    private String apartmentList;
    private String photo;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateCreate;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateUpdate;
}
