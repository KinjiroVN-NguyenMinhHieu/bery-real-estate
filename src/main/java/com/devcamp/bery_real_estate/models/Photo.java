package com.devcamp.bery_real_estate.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import com.devcamp.bery_real_estate.entities.RealEstate;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "photos")
@Data
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;

    @NotEmpty(message = "Photo Url is required")
    private String url;

    @ManyToOne
    @JoinColumn(name = "real_estate_id")
    @JsonIgnore
    private RealEstate realEstate;
}
