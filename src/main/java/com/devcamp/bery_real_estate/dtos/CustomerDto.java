package com.devcamp.bery_real_estate.dtos;

import java.util.Date;

import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CustomerDto {
    private int id;
    private String contactName;
    private String contactTitle;
    private String address;
    private String mobile;

    @Email
    private String email;
    private String note;
    private int createdById;
    private String createdByUserName;
    private int updatedById;
    private String updatedByUserName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;
}
