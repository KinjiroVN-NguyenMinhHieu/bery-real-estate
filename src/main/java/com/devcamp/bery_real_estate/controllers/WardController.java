package com.devcamp.bery_real_estate.controllers;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Ward;
import com.devcamp.bery_real_estate.services.IWardService;

@RestController
@RequestMapping("/")
@CrossOrigin(value = "*", maxAge = -1)
public class WardController {
    @Autowired
    private IWardService wardService;

    /**
     * get all
     * @return
     */
    @GetMapping("/wards")
    public ResponseEntity<List<Ward>> getAllWards() {
        try {
            List<Ward> wards = wardService.getAllWards();
            return new ResponseEntity<>(wards, HttpStatus.OK);
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
    @GetMapping("/wards/{wardId}")
    public ResponseEntity<Object> getWardById(@PathVariable(name = "wardId") Integer id) {
        try {
            Ward ward = wardService.getWardById(id);
            return new ResponseEntity<>(ward, HttpStatus.OK);
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
     * @param pWard
     * @return
     */
    @PostMapping("/wards")
    public ResponseEntity<Object> createWard(@Valid @RequestBody Ward pWard) {
        try {
            Ward ward = wardService.createWard(pWard);
            return new ResponseEntity<>(ward, HttpStatus.CREATED);
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
     * @param pWard
     * @return
     */
    @PutMapping("/wards/{wardId}")
    public ResponseEntity<Object> updateWard(@PathVariable(name = "wardId") Integer id,
            @Valid @RequestBody Ward pWard) {
        try {
            Ward ward = wardService.updateWard(id, pWard);
            return new ResponseEntity<>(ward, HttpStatus.OK);
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
    @DeleteMapping("/wards/{wardId}")
    public ResponseEntity<Object> deleteDistrict(@PathVariable(name = "wardId") Integer id) {
        try {
            wardService.deleteWard(id);
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
