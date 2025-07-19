package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Location;
import com.devcamp.bery_real_estate.repositories.ILocationRepository;
import com.devcamp.bery_real_estate.services.ILocationService;

@Service
public class LocationServiceImpl implements ILocationService {
    @Autowired
    private ILocationRepository locationRepository;

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public Location getLocationById(Integer id) {
        return locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The location is not found"));
    }

    @Override
    public Location createLocation(Location pLocation) {
        Location location = new Location();
        location.setLatitude(pLocation.getLatitude());
        location.setLongitude(pLocation.getLongitude());
        return locationRepository.save(location);
    }

    @Override
    public Location updateLocation(Integer id, Location pLocation) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The location is not found"));

        location.setLatitude(pLocation.getLatitude());
        location.setLongitude(pLocation.getLongitude());
        return locationRepository.save(location);
    }

    @Override
    public void deleteLocation(Integer id) {
        locationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The location is not found"));
        locationRepository.deleteById(id);
    }

}
