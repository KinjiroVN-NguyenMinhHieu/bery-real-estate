package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.AddressMap;
import com.devcamp.bery_real_estate.entities.DesignUnit;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IAddressMapRepository;
import com.devcamp.bery_real_estate.repositories.IDesignUnitRepository;
import com.devcamp.bery_real_estate.services.IDesignUnitService;

@Service
public class DesignUnitServiceImpl implements IDesignUnitService {
    @Autowired
    private IDesignUnitRepository designUnitRepository;
    @Autowired
    private IAddressMapRepository addressMapRepository;

    @Override
    public List<DesignUnit> getListDesignUnits() {
        return designUnitRepository.findAll();
    };

    @Override
    public Page<DesignUnit> getAllDesignUnits(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return designUnitRepository.findAll(pageable);
    };

    @Override
    public DesignUnit getDesignUnitById(Integer id) {
        return designUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Design Unit is not found"));
    };

    @Override
    public DesignUnit createDesignUnit(Integer addressId,
            DesignUnit pDesignUnit) {
        AddressMap addressMap = addressMapRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("The Address Map is not found"));

        DesignUnit designUnit = new DesignUnit();
        designUnit.setName(pDesignUnit.getName());
        designUnit.setDescription(pDesignUnit.getDescription());
        designUnit.setAddress(addressMap);
        designUnit.setPhone(pDesignUnit.getPhone());
        designUnit.setPhone2(pDesignUnit.getPhone2());
        designUnit.setFax(pDesignUnit.getFax());
        designUnit.setEmail(pDesignUnit.getEmail());
        designUnit.setWebsite(pDesignUnit.getWebsite());
        designUnit.setNote(pDesignUnit.getNote());
        return designUnitRepository.save(designUnit);
    };

    @Override
    public DesignUnit updateDesignUnit(Integer addressId, Integer id,
            DesignUnit pDesignUnit) {
        DesignUnit designUnit = designUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Design Unit is not found"));
        AddressMap addressMap = addressMapRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("The Address Map is not found"));

        designUnit.setName(pDesignUnit.getName());
        designUnit.setDescription(pDesignUnit.getDescription());
        designUnit.setAddress(addressMap);
        designUnit.setPhone(pDesignUnit.getPhone());
        designUnit.setPhone2(pDesignUnit.getPhone2());
        designUnit.setFax(pDesignUnit.getFax());
        designUnit.setEmail(pDesignUnit.getEmail());
        designUnit.setWebsite(pDesignUnit.getWebsite());
        designUnit.setNote(pDesignUnit.getNote());
        return designUnitRepository.save(designUnit);
    };

    @Override
    public void deleteDesignUnit(Integer id) {
        designUnitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Design Unit is not found"));
        designUnitRepository.deleteById(id);
    };
}
