package com.devcamp.bery_real_estate.controllers;

import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;
import com.devcamp.bery_real_estate.services.IDistrictService;

@RestController
@RequestMapping("/")
@CrossOrigin(value = "*", maxAge = -1)
public class DistrictController {
    @Autowired
    private IDistrictService districtService;

    /**
     * get all
     * @return
     */
    @GetMapping("/districts")
    public ResponseEntity<List<District>> getAllDistricts() {
        try {
            List<District> districts = districtService.getAllDistricts();
            return new ResponseEntity<>(districts, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get wards by id
     * @param id
     * @return
     */
    @GetMapping("/district/wards")
    public ResponseEntity<Object> getWardsByDistrictId(
            @RequestParam(value = "districtId", required = true) Integer id) {
        try {
            Set<Ward> wards = districtService.getWardsByDistrictId(id);
            return new ResponseEntity<>(wards, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get streets by id
     * @param id
     * @return
     */
    @GetMapping("/district/streets")
    public ResponseEntity<Object> getStreetsByDistrictId(
            @RequestParam(value = "districtId", required = true) Integer id) {
        try {
            Set<Street> streets = districtService.getStreetsByDistrictId(id);
            return new ResponseEntity<>(streets, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get project by id
     * @param id
     * @return
     */
    @GetMapping("/district/projects")
    public ResponseEntity<Object> getProjectsByDistrictId(
            @RequestParam(value = "districtId", required = true) Integer id) {
        try {
            Set<Project> projects = districtService.getProjectsByDistrictId(id);
            return new ResponseEntity<>(projects, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
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
    @GetMapping("/districts/{districtId}")
    public ResponseEntity<Object> getDistrictById(@PathVariable(name = "districtId") Integer id) {
        try {
            District district = districtService.getDistrictById(id);
            return new ResponseEntity<>(district, HttpStatus.OK);
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
     * @param pDistrict
     * @return
     */
    @PostMapping("/districts")
    public ResponseEntity<Object> createDistrict(@Valid @RequestBody District pDistrict) {
        try {
            District district = districtService.createDistrict(pDistrict);
            return new ResponseEntity<>(district, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            System.err.println(e.getMessage());
            // return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param id
     * @param pDistrict
     * @return
     */
    @PutMapping("/districts/{districtId}")
    public ResponseEntity<Object> updateDistrict(@PathVariable(name = "districtId") Integer id,
            @Valid @RequestBody District pDistrict) {
        try {
            District district = districtService.updateDistrict(id, pDistrict);
            return new ResponseEntity<>(district, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete
     */
    @DeleteMapping("/districts/{districtId}")
    public ResponseEntity<Object> deleteDistrict(@PathVariable(name = "districtId") Integer id) {
        try {
            districtService.deleteDistrict(id);
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
