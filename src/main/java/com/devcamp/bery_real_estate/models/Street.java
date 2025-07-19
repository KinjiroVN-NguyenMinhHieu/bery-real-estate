package com.devcamp.bery_real_estate.models;

import java.util.Date;
import java.util.Set;

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

import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "street")
public class Street {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty(message = "Enter the name of the street")
    @Size(min = 1, max = 50, message = "The name of the street must have a length of 1 to 50 characters")
    @Column(name = "_name")
    private String name;

    @NotEmpty(message = "Entering the prefix of the street")
    @Size(min = 1, max = 20, message = "Street prefix must have a length of 1 to 20 characters")
    @Column(name = "_prefix")
    private String prefix;

    @NotNull(message = "Enter district corresponding")
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "_district_id")
    private District district;

    @NotNull(message = "Enter province/city corresponding")
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "_province_id")
    private Province province;

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

    @OneToMany(targetEntity = Project.class, mappedBy = "street", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Project> projects;

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "province", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<RealEstate> realEstates;

    @Transient
    private int districtId;

    @Transient
    private String districtName;

    @Transient
    private int provinceId;

    @Transient
    private String provinceName;

    public int getDistrictId() {
        return this.district.getId();
    }

    public void setDistrictId(int districtId) {
        this.district.setId(districtId);
    }

    public String getDistrictName() {
        return this.district.getName();
    }

    public void setDistrictName(String districtName) {
        this.district.setName(districtName);
    }

    public int getProvinceId() {
        return this.district.getProvinceId();
    }

    public void setProvinceId(int provinceId) {
        this.district.setProvinceId(provinceId);
        ;
    }

    public String getProvinceName() {
        return this.district.getProvinceName();
    }

    public void setProvinceName(String provinceName) {
        this.district.setProvinceName(provinceName);
    }

    public Street() {
    }

    public Street(int id,
            @NotEmpty(message = "Enter the name of the street") @Size(min = 1, max = 50, message = "The name of the street must have a length of 1 to 50 characters") String name,
            @NotEmpty(message = "Entering the prefix of the street") @Size(min = 1, max = 20, message = "Street prefix must have a length of 1 to 20 characters") String prefix,
            @NotNull(message = "Enter district corresponding") District district,
            @NotNull(message = "Enter province/city corresponding") Province province, Date createdAt, Date updatedAt,
            Set<Project> projects, Set<RealEstate> realEstates) {
        this.id = id;
        this.name = name;
        this.prefix = prefix;
        this.district = district;
        this.province = province;
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

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
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
