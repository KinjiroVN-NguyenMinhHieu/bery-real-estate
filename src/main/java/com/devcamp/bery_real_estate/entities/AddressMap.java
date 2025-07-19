package com.devcamp.bery_real_estate.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "address_map")
public class AddressMap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String address;

    @Column(name = "_lat", nullable = false)
    private double latitude;

    @Column(name = "_lng", nullable = false)
    private double longitude;

    @OneToMany(targetEntity = Investor.class, mappedBy = "address", cascade = CascadeType.ALL)
    private Set<Investor> investors;

    @OneToMany(targetEntity = DesignUnit.class, mappedBy = "address", cascade = CascadeType.ALL)
    private Set<DesignUnit> designUnits;

    @OneToMany(targetEntity = ConstructionContractor.class, mappedBy = "address", cascade = CascadeType.ALL)
    private Set<ConstructionContractor> constructionContractors;

    public AddressMap() {
    }

    public AddressMap(int id, String address, double latitude, double longitude, Set<Investor> investors,
            Set<DesignUnit> designUnits, Set<ConstructionContractor> constructionContractors) {
        this.id = id;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.investors = investors;
        this.designUnits = designUnits;
        this.constructionContractors = constructionContractors;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Set<Investor> getInvestors() {
        return investors;
    }

    public void setInvestors(Set<Investor> investors) {
        this.investors = investors;
    }

    public Set<DesignUnit> getDesignUnits() {
        return designUnits;
    }

    public void setDesignUnits(Set<DesignUnit> designUnits) {
        this.designUnits = designUnits;
    }

    public Set<ConstructionContractor> getConstructionContractors() {
        return constructionContractors;
    }

    public void setConstructionContractors(Set<ConstructionContractor> constructionContractors) {
        this.constructionContractors = constructionContractors;
    }

    
}
