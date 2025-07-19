package com.devcamp.bery_real_estate.models;

import java.util.Date;
import java.util.Set;

import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "province")
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Enter the provincial/city code")
    @Size(min = 1, max = 10, message = "The provincial/city code must have a length of 1 to 10 characters")
    @Column(name = "_code")
    private String code;

    @NotEmpty(message = "Enter the name of the province/city")
    @Size(min = 1, max = 50, message = "The name of the province/city must have a length of 1 to 50 characters")
    @Column(name = "_name")
    private String name;

    @OneToMany(targetEntity = District.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<District> districts;

    @OneToMany(targetEntity = Ward.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Ward> wards;

    @OneToMany(targetEntity = Street.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Street> streets;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @CreatedDate
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @LastModifiedDate
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;

    @OneToMany(targetEntity = Project.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Project> projects;

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RealEstate> realEstates;

    public Province() {
    }

    public Province(int id,
            @NotEmpty(message = "Enter the provincial/city code") @Size(min = 1, max = 10, message = "The provincial/city code must have a length of 1 to 10 characters") String code,
            @NotEmpty(message = "Enter the name of the province/city") @Size(min = 1, max = 50, message = "The name of the province/city must have a length of 1 to 50 characters") String name,
            Set<District> districts, Set<Ward> wards, Set<Street> streets, Date createdAt, Date updatedAt,
            Set<Project> projects, Set<RealEstate> realEstates) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.districts = districts;
        this.wards = wards;
        this.streets = streets;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.projects = projects;
        this.realEstates = realEstates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<District> getDistricts() {
        return districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }

    public Set<Ward> getWards() {
        return wards;
    }

    public void setWards(Set<Ward> wards) {
        this.wards = wards;
    }

    public Set<Street> getStreets() {
        return streets;
    }

    public void setStreets(Set<Street> streets) {
        this.streets = streets;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public Set<RealEstate> getRealEstates() {
        return realEstates;
    }

    public void setRealEstates(Set<RealEstate> realEstates) {
        this.realEstates = realEstates;
    }

}
