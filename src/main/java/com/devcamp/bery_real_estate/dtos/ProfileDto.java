package com.devcamp.bery_real_estate.dtos;

import java.util.Date;

import javax.validation.constraints.Email;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ProfileDto {
    private String lastName;
    private String firstName;
    private String userName;

    @Email
    private String email;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthDate;
    private String address;
    private String city;
    private String country;
    private String homePhone;
    private String note;
    private String photo;
    //trường file lưu ảnh
    private MultipartFile photoFile;
}
