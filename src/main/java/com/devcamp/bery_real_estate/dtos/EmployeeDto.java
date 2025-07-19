package com.devcamp.bery_real_estate.dtos;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Email;

import com.devcamp.bery_real_estate.constants.EActivated;
import com.devcamp.bery_real_estate.models.Role;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class EmployeeDto {
    private int id;
    private String lastName;
    private String firstName;
    private String userName;
    @JsonIgnore
    private String password;

    @Email
    private String email;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthDate;
    private String address;
    private String city;
    private String country;
    private String homePhone;
    private String photo;
    private String note;
    private EActivated activated;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;
    private Set<Role> roles;
}
