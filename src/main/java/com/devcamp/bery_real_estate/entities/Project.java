package com.devcamp.bery_real_estate.entities;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;

@Entity
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;

    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;

    @ManyToOne
    @JoinColumn(name = "ward_id")
    private Ward ward;

    @ManyToOne
    @JoinColumn(name = "street_id")
    private Street street;

    @Column(name = "address")
    private String address;

    @Column(name = "slogan")
    private String slogan;

    @Column(name = "description")
    private String description;

    @Column(name = "acreage")
    private BigDecimal acreage;

    @Column(name = "construct_area")
    private BigDecimal constructArea;

    @Column(name = "num_block")
    private Short numBlock;

    @Column(name = "num_floors")
    private String numFloors;

    @Column(name = "num_apartment", nullable = false)
    private int numApartment;

    @Column(name = "apartmentt_area")
    private String apartmenttArea;

    @ManyToOne
    @JoinColumn(name = "investor", nullable = false)
    private Investor investor;

    @ManyToOne
    @JoinColumn(name = "construction_contractor")
    private ConstructionContractor constructionContractor;

    @ManyToOne
    @JoinColumn(name = "design_unit")
    private DesignUnit designUnit;

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "project", cascade = CascadeType.ALL)
    private Set<RealEstate> realEstates;

    @OneToMany(targetEntity = MasterLayout.class, mappedBy = "project", cascade = CascadeType.ALL)
    private Set<MasterLayout> masterLayouts;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "project_utility_rel", joinColumns = { @JoinColumn(name = "project_id") }, inverseJoinColumns = {
            @JoinColumn(name = "utility_id") })
    private Set<Utility> utilities;

    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "project_regionlink_rel", joinColumns = {
            @JoinColumn(name = "project_id") }, inverseJoinColumns = { @JoinColumn(name = "regionlink_id") })
    private Set<RegionLink> regionLinks;

    @Column(name = "photo")
    private String photo;

    @Column(name = "_lat")
    private Double latitude;

    @Column(name = "_lng")
    private Double longitude;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;

    public Project() {
    }

    public Project(int id, String name, Province province, District district, Ward ward, Street street, String address,
            String slogan, String description, BigDecimal acreage, BigDecimal constructArea, Short numBlock,
            String numFloors, int numApartment, String apartmenttArea, Investor investor,
            ConstructionContractor constructionContractor, DesignUnit designUnit, Set<RealEstate> realEstates,
            Set<MasterLayout> masterLayouts, Set<Utility> utilities, Set<RegionLink> regionLinks, String photo,
            Double latitude, Double longitude, boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.address = address;
        this.slogan = slogan;
        this.description = description;
        this.acreage = acreage;
        this.constructArea = constructArea;
        this.numBlock = numBlock;
        this.numFloors = numFloors;
        this.numApartment = numApartment;
        this.apartmenttArea = apartmenttArea;
        this.investor = investor;
        this.constructionContractor = constructionContractor;
        this.designUnit = designUnit;
        this.realEstates = realEstates;
        this.masterLayouts = masterLayouts;
        this.utilities = utilities;
        this.regionLinks = regionLinks;
        this.photo = photo;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isDeleted = isDeleted;
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

    public Province getProvince() {
        return province;
    }

    public void setProvince(Province province) {
        this.province = province;
    }

    public District getDistrict() {
        return district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    public Ward getWard() {
        return ward;
    }

    public void setWard(Ward ward) {
        this.ward = ward;
    }

    public Street getStreet() {
        return street;
    }

    public void setStreet(Street street) {
        this.street = street;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAcreage() {
        return acreage;
    }

    public void setAcreage(BigDecimal acreage) {
        this.acreage = acreage;
    }

    public BigDecimal getConstructArea() {
        return constructArea;
    }

    public void setConstructArea(BigDecimal constructArea) {
        this.constructArea = constructArea;
    }

    public Short getNumBlock() {
        return numBlock;
    }

    public void setNumBlock(Short numBlock) {
        this.numBlock = numBlock;
    }

    public String getNumFloors() {
        return numFloors;
    }

    public void setNumFloors(String numFloors) {
        this.numFloors = numFloors;
    }

    public int getNumApartment() {
        return numApartment;
    }

    public void setNumApartment(int numApartment) {
        this.numApartment = numApartment;
    }

    public String getApartmenttArea() {
        return apartmenttArea;
    }

    public void setApartmenttArea(String apartmenttArea) {
        this.apartmenttArea = apartmenttArea;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public ConstructionContractor getConstructionContractor() {
        return constructionContractor;
    }

    public void setConstructionContractor(ConstructionContractor constructionContractor) {
        this.constructionContractor = constructionContractor;
    }

    public DesignUnit getDesignUnit() {
        return designUnit;
    }

    public void setDesignUnit(DesignUnit designUnit) {
        this.designUnit = designUnit;
    }

    public Set<RealEstate> getRealEstates() {
        return realEstates;
    }

    public void setRealEstates(Set<RealEstate> realEstates) {
        this.realEstates = realEstates;
    }

    public Set<MasterLayout> getMasterLayouts() {
        return masterLayouts;
    }

    public void setMasterLayouts(Set<MasterLayout> masterLayouts) {
        this.masterLayouts = masterLayouts;
    }

    public Set<Utility> getUtilities() {
        return utilities;
    }

    public void setUtilities(Set<Utility> utilities) {
        this.utilities = utilities;
    }

    public Set<RegionLink> getRegionLinks() {
        return regionLinks;
    }

    public void setRegionLinks(Set<RegionLink> regionLinks) {
        this.regionLinks = regionLinks;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
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

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
