package com.devcamp.bery_real_estate.entities;

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

@Entity
@Table(name = "design_unit")
public class DesignUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;
    private String description;

    @OneToMany(targetEntity = Project.class, mappedBy = "designUnit", cascade = CascadeType.ALL)
    private Set<Project> projects;

    @ManyToOne
    @JoinColumn(name = "address")
    private AddressMap address;
    private String phone;
    private String phone2;
    private String fax;
    private String email;
    private String website;
    private String note;

    public DesignUnit() {
    }

    public DesignUnit(int id, String name, String description, Set<Project> projects, AddressMap address, String phone,
            String phone2, String fax, String email, String website, String note) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.projects = projects;
        this.address = address;
        this.phone = phone;
        this.phone2 = phone2;
        this.fax = fax;
        this.email = email;
        this.website = website;
        this.note = note;
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

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

    public AddressMap getAddress() {
        return address;
    }

    public void setAddress(AddressMap address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhone2() {
        return phone2;
    }

    public void setPhone2(String phone2) {
        this.phone2 = phone2;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

}
