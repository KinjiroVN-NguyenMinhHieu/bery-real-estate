package com.devcamp.bery_real_estate.entities;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.devcamp.bery_real_estate.constants.EActivated;
import com.devcamp.bery_real_estate.models.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "employees")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "user_name", unique = true)
    private String userName;

    private String password;

    @Column(unique = true)
    private String email;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "birth_date")
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date birthDate;

    private String address;

    private String city;

    private String country;

    @Column(name = "home_phone")
    private String homePhone;

    private String photo;

    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EActivated activated = EActivated.Y;

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

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "employee", cascade = CascadeType.ALL)
    private Set<RealEstate> realEstates;

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private Set<RealEstate> createdRealEstates;

    @OneToMany(targetEntity = RealEstate.class, mappedBy = "updatedBy", cascade = CascadeType.ALL)
    private Set<RealEstate> updatedRealEstates;

    @OneToMany(targetEntity = Customer.class, mappedBy = "createdBy", cascade = CascadeType.ALL)
    private Set<Customer> createdCustomers;

    @OneToMany(targetEntity = Customer.class, mappedBy = "updatedBy", cascade = CascadeType.ALL)
    private Set<Customer> updatedCustomers;

    //ko dùng persist vì sẽ gây lỗi detached entity
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "employee_role_rel", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "employee_estate_rel", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "estate_id"))
    private Set<RealEstate> favouriteRealEstates = new HashSet<>();

    public Employee() {
    }

    public Employee(String userName, String email, String password) {
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    public Employee(int id, String lastName, String firstName, String userName, String password, String email,
            Date birthDate, String address, String city, String country, String homePhone, String photo, String note,
            EActivated activated, Date createdAt, Date updatedAt, Set<RealEstate> realEstates,
            Set<RealEstate> createdRealEstates, Set<RealEstate> updatedRealEstates, Set<Customer> createdCustomers,
            Set<Customer> updatedCustomers, Set<Role> roles, Set<RealEstate> favouriteRealEstates) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.birthDate = birthDate;
        this.address = address;
        this.city = city;
        this.country = country;
        this.homePhone = homePhone;
        this.photo = photo;
        this.note = note;
        this.activated = activated;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.realEstates = realEstates;
        this.createdRealEstates = createdRealEstates;
        this.updatedRealEstates = updatedRealEstates;
        this.createdCustomers = createdCustomers;
        this.updatedCustomers = updatedCustomers;
        this.roles = roles;
        this.favouriteRealEstates = favouriteRealEstates;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public EActivated getActivated() {
        return activated;
    }

    public void setActivated(EActivated activated) {
        this.activated = activated;
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

    public Set<RealEstate> getRealEstates() {
        return realEstates;
    }

    public void setRealEstates(Set<RealEstate> realEstates) {
        this.realEstates = realEstates;
    }

    public Set<RealEstate> getCreatedRealEstates() {
        return createdRealEstates;
    }

    public void setCreatedRealEstates(Set<RealEstate> createdRealEstates) {
        this.createdRealEstates = createdRealEstates;
    }

    public Set<RealEstate> getUpdatedRealEstates() {
        return updatedRealEstates;
    }

    public void setUpdatedRealEstates(Set<RealEstate> updatedRealEstates) {
        this.updatedRealEstates = updatedRealEstates;
    }

    public Set<Customer> getCreatedCustomers() {
        return createdCustomers;
    }

    public void setCreatedCustomers(Set<Customer> createdCustomers) {
        this.createdCustomers = createdCustomers;
    }

    public Set<Customer> getUpdatedCustomers() {
        return updatedCustomers;
    }

    public void setUpdatedCustomers(Set<Customer> updatedCustomers) {
        this.updatedCustomers = updatedCustomers;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<RealEstate> getFavouriteRealEstates() {
        return favouriteRealEstates;
    }

    public void setFavouriteRealEstates(Set<RealEstate> favouriteRealEstates) {
        this.favouriteRealEstates = favouriteRealEstates;
    }

}
