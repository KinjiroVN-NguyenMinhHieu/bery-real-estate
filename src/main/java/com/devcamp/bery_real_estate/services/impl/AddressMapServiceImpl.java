package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.AddressMap;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IAddressMapRepository;
import com.devcamp.bery_real_estate.services.IAddressMapService;

@Service
public class AddressMapServiceImpl implements IAddressMapService {
    @Autowired
    private IAddressMapRepository addressMapRepository;

    @Override
    public List<AddressMap> getListAddressMaps() {
        return addressMapRepository.findAll();
    };

    @Override
    public Page<AddressMap> getAllAddressMaps(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return addressMapRepository.findAll(pageable);
    };

    @Override
    public AddressMap getAddressMapById(Integer id) {
        return addressMapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The address map is not found"));
    };

    @Override
    public AddressMap createAddressMap(AddressMap pAddressMap) {
        AddressMap addressMap = new AddressMap();
        addressMap.setAddress(pAddressMap.getAddress());
        addressMap.setLatitude(pAddressMap.getLatitude());
        addressMap.setLongitude(pAddressMap.getLongitude());
        return addressMapRepository.save(addressMap);
    };

    @Override
    public AddressMap updateAddressMap(Integer id, AddressMap pAddressMap) {
        AddressMap addressMap = addressMapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The address map is not found"));
        addressMap.setAddress(pAddressMap.getAddress());
        addressMap.setLatitude(pAddressMap.getLatitude());
        addressMap.setLongitude(pAddressMap.getLongitude());
        return addressMapRepository.save(addressMap);
    };

    @Override
    public void deleteAddressMap(Integer id) {
        addressMapRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The address map is not found"));
        addressMapRepository.deleteById(id);
    };
}
