package com.devcamp.bery_real_estate.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "master_layout")
public class MasterLayout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;
    private String description;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    private BigDecimal acreage;

    @Column(name = "apartment_list")
    private String apartmentList;

    private String photo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_create", updatable = false)
    @CreatedDate
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateCreate = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date_update")
    @LastModifiedDate
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date dateUpdate;

    public MasterLayout() {
    }

    public MasterLayout(int id, String name, String description, Project project, BigDecimal acreage,
            String apartmentList, String photo, Date dateCreate, Date dateUpdate) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.project = project;
        this.acreage = acreage;
        this.apartmentList = apartmentList;
        this.photo = photo;
        this.dateCreate = dateCreate;
        this.dateUpdate = dateUpdate;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public BigDecimal getAcreage() {
        return acreage;
    }

    public void setAcreage(BigDecimal acreage) {
        this.acreage = acreage;
    }

    public String getApartmentList() {
        return apartmentList;
    }

    public void setApartmentList(String apartmentList) {
        this.apartmentList = apartmentList;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Date getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(Date dateCreate) {
        this.dateCreate = dateCreate;
    }

    public Date getDateUpdate() {
        return dateUpdate;
    }

    public void setDateUpdate(Date dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

}
