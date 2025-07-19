package com.devcamp.bery_real_estate.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devcamp.bery_real_estate.dtos.ProjectDto;
import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IProjectService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class ProjectController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IProjectService projectService;

    /**
     * get list
     * @return
     */
    @GetMapping("/projects/all")
    public ResponseEntity<List<ProjectDto>> getListProjects() {
        try {
            List<ProjectDto> projectDtos = projectService
                    .getListProjects().stream()
                    .map(project -> modelMapper.map(project, ProjectDto.class)).toList();
            return new ResponseEntity<>(projectDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get all(pagination)
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/projects")
    public ResponseEntity<Page<ProjectDto>> getAllProjects(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        try {
            Page<ProjectDto> projectDtos = projectService
                    .getAllProjects(page, size)
                    .map(project -> modelMapper.map(project, ProjectDto.class));
            return new ResponseEntity<>(projectDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by id
     * @param id
     * @return
     */
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Object> getProjectById(
            @PathVariable(name = "projectId", required = true) Integer id) {
        try {
            Project project = projectService.getProjectById(id);
            // convert sang dto
            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            return new ResponseEntity<>(projectDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by district id
     * @param id
     * @return
     */
    @GetMapping("/district/{districtId}/projects")
    public ResponseEntity<Object> getProjectByDistrictId(
            @PathVariable(name = "districtId", required = true) Integer id) {
        try {
            Set<Project> projects = projectService.getProjectsByDistrictId(id);
            // convert sang dto
            List<ProjectDto> projectDto = projects.stream()
                    .map(project -> modelMapper.map(project, ProjectDto.class))
                    .toList();
            return new ResponseEntity<>(projectDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * add
     * @param pProjectDto
     * @return
     */
    @PostMapping("/projects")
    public ResponseEntity<Object> createProject(@Valid @RequestBody ProjectDto pProjectDto) {
        try {
            // convert sang entity
            Project pProject = modelMapper.map(pProjectDto, Project.class);
            // xử lý dữ liệu
            Project project = projectService.createProject(pProject);
            // convert sang dto
            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            return new ResponseEntity<>(projectDto, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param id
     * @param pProjectDto
     * @return
     */
    @PutMapping("/projects/{projectId}")
    public ResponseEntity<Object> updateProject(@PathVariable(name = "projectId", required = true) Integer id,
            @Valid @RequestBody ProjectDto pProjectDto) {
        try {
            // convert sang entity
            Project pProject = modelMapper.map(pProjectDto, Project.class);
            // xử lý dữ liệu
            Project project = projectService.updateProject(id, pProject);
            // convert sang dto
            ProjectDto projectDto = modelMapper.map(project, ProjectDto.class);
            return new ResponseEntity<>(projectDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete
     * @param id
     * @return
     */
    @DeleteMapping("/projects/{projectId}")
    public ResponseEntity<Object> deleteProject(@PathVariable(name = "projectId") Integer id) {
        try {
            projectService.deleteProject(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
