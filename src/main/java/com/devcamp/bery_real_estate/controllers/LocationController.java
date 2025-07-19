package com.devcamp.bery_real_estate.controllers;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.devcamp.bery_real_estate.models.Location;
import com.devcamp.bery_real_estate.services.ILocationService;



@RestController
@CrossOrigin
@RequestMapping("/")
public class LocationController {
    @Autowired
    private ILocationService locationService;

    /**
     * get all
     * @return
     */
    @GetMapping("/locations")
    public ResponseEntity<List<Location>> getAllLocations() {
        try {
            List<Location> locations = locationService.getAllLocations();
            return new ResponseEntity<>(locations, HttpStatus.OK);
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
    @GetMapping("/locations/{locationId}")
    public ResponseEntity<Object> getLocationById(@PathVariable(name = "locationId", required = true) Integer id) {
        try {
            Location location = locationService.getLocationById(id);
            return new ResponseEntity<>(location, HttpStatus.OK);
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
     * @param pLocation
     * @return
     */
    @PostMapping("/locations")
    public ResponseEntity<Object> createLocation(@Valid @RequestBody Location pLocation) {
        try {
            Location location = locationService.createLocation(pLocation);
            return new ResponseEntity<>(location, HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param id
     * @param pLocation
     * @return
     */
    @PutMapping("/locations/{locationId}")
    public ResponseEntity<Object> updateLocation(@PathVariable(name = "locationId") Integer id,
            @Valid @RequestBody Location pLocation) {
        try {
            Location location = locationService.updateLocation(id, pLocation);
            return new ResponseEntity<>(location, HttpStatus.OK);
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
    @DeleteMapping("/locations/{locationId}")
    public ResponseEntity<Object> deleteLocation(@PathVariable(name = "locationId") Integer id) {
        try {
            locationService.deleteLocation(id);
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
