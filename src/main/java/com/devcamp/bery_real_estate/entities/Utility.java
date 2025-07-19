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
@Table(name = "utilities")
public class Utility {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String name;
    private String description;
    private String photo;

    @ManyToMany(mappedBy = "utilities", fetch = FetchType.LAZY, cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    private Set<Project> projects;

    public Utility() {
    }

    public Utility(int id, String name, String description, String photo, Set<Project> projects) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photo = photo;
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

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }

}
