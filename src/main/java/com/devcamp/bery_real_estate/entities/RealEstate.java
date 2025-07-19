package com.devcamp.bery_real_estate.entities;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import com.devcamp.bery_real_estate.constants.EDirection;
import com.devcamp.bery_real_estate.constants.EFurnitureType;
import com.devcamp.bery_real_estate.constants.ERequest;
import com.devcamp.bery_real_estate.constants.EStatus;
import com.devcamp.bery_real_estate.constants.EType;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Photo;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "realestate")
public class RealEstate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ERequest request;

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

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @Column(nullable = false)
    private String address;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    private long price;
    private BigDecimal acreage;

    @Enumerated(EnumType.STRING)
    private EDirection direction;

    @Column(name = "apart_code")
    private String apartCode;

    private byte bedroom;

    @Enumerated(EnumType.STRING)
    @Column(name = "furniture_type")
    private EFurnitureType furnitureType;

    @Column(name = "price_rent")
    private int priceRent;

    private String description;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EStatus status = EStatus.PENDING;

    @OneToMany(targetEntity = Photo.class, mappedBy = "realEstate", cascade = CascadeType.ALL)
    private List<Photo> photos;

    @ManyToOne
    @JoinColumn(name = "created_by")
    @CreatedBy
    private Employee createdBy;

    @ManyToOne
    @JoinColumn(name = "updated_by")
    @LastModifiedBy
    private Employee updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    @CreatedDate
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    @LastModifiedDate
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;

    public RealEstate() {
    }

    public RealEstate(int id, String title, EType type, ERequest request, Province province, District district,
            Ward ward, Street street, Project project, String address, Employee employee, long price,
            BigDecimal acreage, EDirection direction, String apartCode, byte bedroom, EFurnitureType furnitureType,
            int priceRent, String description, boolean isDeleted, EStatus status, List<Photo> photos,
            Employee createdBy, Employee updatedBy, Date createdAt, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.request = request;
        this.province = province;
        this.district = district;
        this.ward = ward;
        this.street = street;
        this.project = project;
        this.address = address;
        this.employee = employee;
        this.price = price;
        this.acreage = acreage;
        this.direction = direction;
        this.apartCode = apartCode;
        this.bedroom = bedroom;
        this.furnitureType = furnitureType;
        this.priceRent = priceRent;
        this.description = description;
        this.isDeleted = isDeleted;
        this.status = status;
        this.photos = photos;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EType getType() {
        return type;
    }

    public void setType(EType type) {
        this.type = type;
    }

    public ERequest getRequest() {
        return request;
    }

    public void setRequest(ERequest request) {
        this.request = request;
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

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public BigDecimal getAcreage() {
        return acreage;
    }

    public void setAcreage(BigDecimal acreage) {
        this.acreage = acreage;
    }

    public EDirection getDirection() {
        return direction;
    }

    public void setDirection(EDirection direction) {
        this.direction = direction;
    }

    public String getApartCode() {
        return apartCode;
    }

    public void setApartCode(String apartCode) {
        this.apartCode = apartCode;
    }

    public byte getBedroom() {
        return bedroom;
    }

    public void setBedroom(byte bedroom) {
        this.bedroom = bedroom;
    }

    public EFurnitureType getFurnitureType() {
        return furnitureType;
    }

    public void setFurnitureType(EFurnitureType furnitureType) {
        this.furnitureType = furnitureType;
    }

    public int getPriceRent() {
        return priceRent;
    }

    public void setPriceRent(int priceRent) {
        this.priceRent = priceRent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Employee getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Employee updatedBy) {
        this.updatedBy = updatedBy;
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

    
}
