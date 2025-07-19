package com.devcamp.bery_real_estate.services;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.Project;

public interface IProjectService {
    /**
     * get list
     * @return
     */
    List<Project> getListProjects();

    /**
     * get all(pagination)
     * @param page
     * @param size
     * @return
     */
    Page<Project> getAllProjects(int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    Project getProjectById(Integer id);

    /**
     * get projects by district id
     * @param id
     * @return
     */
    Set<Project> getProjectsByDistrictId(Integer id);

    /**
     * add
     * @param pProject
     * @return
     */
    Project createProject(Project pProject);

    /**
     * update
     * @param id
     * @param pProject
     * @return
     */
    Project updateProject(Integer id, Project pProject);

    /**
     * delete
     * @param id
     */
    void deleteProject(Integer id);
}
