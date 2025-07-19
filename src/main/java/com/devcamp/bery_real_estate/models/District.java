package com.devcamp.bery_real_estate.models;

import java.util.Date;
import java.util.Set;

import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "district")
public class District {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Enter the name of the district")
    @Size(min = 1, max = 50, message = "The district's name must have a length of 1 to 50 characters")
    @Column(name = "_name")
    private String name;

    @NotEmpty(message = "Enter the prefix district/district")
    @Size(min = 1, max = 20, message = "District/district prefix must have a length of 1 to 20 characters")
    @Column(name = "_prefix")
    private String prefix;

    @NotNull(message = "Enter the province/ city corresponding")
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "_province_id")
    private Province province;

    @OneToMany(targetEntity = Ward.class, mappedBy = "district", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Ward> wards;

    @OneToMany(targetEntity = Street.class, mappedBy = "district", cascade = CascadeType.ALL)
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

    @OneToMany(targetEntity = Project.class, mappedBy = "district", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Project> projects;

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RealEstate> realEstates;

    @Transient
    private int provinceId;

    @Transient
    private String provinceName;

    public int getProvinceId() {
        return this.province.getId();
    }

    public void setProvinceId(int provinceId) {
        this.province.setId(provinceId);
    }

    public String getProvinceName() {
        return this.province.getName();
    }

    public void setProvinceName(String provinceName) {
        this.province.setName(provinceName);
    }

    public District() {
    }

    public District(int id,
            @NotEmpty(message = "Enter the name of the district") @Size(min = 1, max = 50, message = "The district's name must have a length of 1 to 50 characters") String name,
            @NotEmpty(message = "Enter the prefix district/district") @Size(min = 1, max = 20, message = "District/district prefix must have a length of 1 to 20 characters") String prefix,
            @NotNull(message = "Enter the province/ city corresponding") Province province, Set<Ward> wards,
            Set<Street> streets, Date createdAt, Date updatedAt, Set<Project> projects, Set<RealEstate> realEstates) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.province = province;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
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
