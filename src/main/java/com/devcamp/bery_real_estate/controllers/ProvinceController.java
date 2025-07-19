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

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.services.IProvinceService;

@RestController
@RequestMapping("/")
@CrossOrigin
public class ProvinceController {
    @Autowired
    private IProvinceService provinceService;

    /**
     * get all
     * @return
     */
    @GetMapping("/provinces")
    public ResponseEntity<List<Province>> getAllProvinces() {
        try {
            List<Province> provinces = provinceService.getAllProvinces();
            return new ResponseEntity<>(provinces, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by province id
     * @param id
     * @return
     */
    @GetMapping("/province/districts")
    public ResponseEntity<Object> getDistrictsByProvinceId(
            @RequestParam(value = "provinceId", required = true) Integer id) {
        try {
            Set<District> districts = provinceService.getDistrictsByProvinceId(id);
            return new ResponseEntity<>(districts, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
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
    @GetMapping("/provinces/pagination")
    public ResponseEntity<Object> getProvincesPanigation(
            @RequestParam(value = "page", defaultValue = "0") String page,
            @RequestParam(value = "size", defaultValue = "5") String size) {
        try {
            List<Province> provinces = provinceService.getProvincesPanigation(page, size);
            return new ResponseEntity<>(provinces, HttpStatus.OK);
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
    @GetMapping("/provinces/{provinceId}")
    public ResponseEntity<Object> getProvinceById(@PathVariable(name = "provinceId", required = true) Integer id) {
        try {
            Province province = provinceService.getProvinceById(id);
            return new ResponseEntity<>(province, HttpStatus.OK);
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
     * @param pProvince
     * @return
     */
    @PostMapping("/provinces")
    public ResponseEntity<Object> createProvince(@Valid @RequestBody Province pProvince) {
        try {
            Province province = provinceService.createProvince(pProvince);
            return new ResponseEntity<>(province, HttpStatus.CREATED);
        } catch (DuplicateKeyException e) {
            System.err.println(e.getMessage());
            // return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
            return ResponseEntity.unprocessableEntity().body(e.getMessage());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param id
     * @param pProvince
     * @return
     */
    @PutMapping("/provinces/{provinceId}")
    public ResponseEntity<Object> updateProvince(@PathVariable(name = "provinceId") Integer id,
            @Valid @RequestBody Province pProvince) {
        try {
            Province province = provinceService.updateProvine(id, pProvince);
            return new ResponseEntity<>(province, HttpStatus.OK);
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
     * @param id
     * @return
     */
    @DeleteMapping("/provinces/{provinceId}")
    public ResponseEntity<Object> deleteProvince(@PathVariable(name = "provinceId") Integer id) {
        try {
            provinceService.deleteProvince(id);
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