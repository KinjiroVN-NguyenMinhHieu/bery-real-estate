package com.devcamp.bery_real_estate.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name = "region_link")
public class RegionLink {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;
    private String description;
    private String photo;
    private String address;

    @Column(name = "_lat")
    private Double latitude;

    @Column(name = "_lng")
    private Double longitude;

    @ManyToMany(mappedBy = "regionLinks", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Project> projects;

    public RegionLink() {
    }

    public RegionLink(int id, String name, String description, String photo, String address, Double latitude,
        Double longitude, Set<Project> projects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photo = photo;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.projects = projects;
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

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

}
