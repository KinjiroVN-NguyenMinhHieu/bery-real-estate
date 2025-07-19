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
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.services.IStreetService;

@RestController
@RequestMapping("/")
@CrossOrigin(value = "*", maxAge = -1)
public class StreetController {
    @Autowired
    private IStreetService streetService;

    /**
     * get all
     * @return
     */
    @GetMapping("/streets")
    public ResponseEntity<List<Street>> getAllStreets() {
        try {
            List<Street> streets = streetService.getAllStreets();
            return new ResponseEntity<>(streets, HttpStatus.OK);
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
    @GetMapping("/streets/{streetId}")
    public ResponseEntity<Object> getStreetById(@PathVariable(name = "streetId") Integer id) {
        try {
            Street street = streetService.getStreetById(id);
            return new ResponseEntity<>(street, HttpStatus.OK);
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
     * @param pStreet
     * @return
     */
    @PostMapping("/streets")
    public ResponseEntity<Object> createStreet(@Valid @RequestBody Street pStreet) {
        try {
            Street street = streetService.createStreet(pStreet);
            return new ResponseEntity<>(street, HttpStatus.CREATED);
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
     * @param pStreet
     * @return
     */
    @PutMapping("/streets/{streetId}")
    public ResponseEntity<Object> updateStreet(@PathVariable(name = "streetId") Integer id,
            @Valid @RequestBody Street pStreet) {
        try {
            Street street = streetService.updateStreet(id, pStreet);
            return new ResponseEntity<>(street, HttpStatus.OK);
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
    @DeleteMapping("/streets/{streetId}")
    public ResponseEntity<Object> deleteDistrict(@PathVariable(name = "streetId") Integer id) {
        try {
            streetService.deleteStreet(id);
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
