package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.AddressMap;
import com.devcamp.bery_real_estate.entities.ConstructionContractor;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IAddressMapRepository;
import com.devcamp.bery_real_estate.repositories.IConstructionContractorRepository;
import com.devcamp.bery_real_estate.services.IConstructionContractorService;

@Service
public class ConstructionContractorService implements IConstructionContractorService {
    @Autowired
    private IConstructionContractorRepository constructionContractorRepository;
    @Autowired
    private IAddressMapRepository addressMapRepository;

    @Override
    public List<ConstructionContractor> getListConstructionContractors() {
        return constructionContractorRepository.findAll();
    };

    @Override
    public Page<ConstructionContractor> getAllConstructionContractors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return constructionContractorRepository.findAll(pageable);
    };

    @Override
    public ConstructionContractor getConstructionContractorById(Integer id) {
        return constructionContractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Construction Contractor is not found"));
    };

    @Override
    public ConstructionContractor createConstructionContractor(Integer addressId,
            ConstructionContractor pConstructionContractor) {
        AddressMap addressMap = addressMapRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("The Address Map is not found"));

        ConstructionContractor constructionContractor = new ConstructionContractor();
        constructionContractor.setName(pConstructionContractor.getName());
        constructionContractor.setDescription(pConstructionContractor.getDescription());
        constructionContractor.setAddress(addressMap);
        constructionContractor.setPhone(pConstructionContractor.getPhone());
        constructionContractor.setPhone2(pConstructionContractor.getPhone2());
        constructionContractor.setFax(pConstructionContractor.getFax());
        constructionContractor.setEmail(pConstructionContractor.getEmail());
        constructionContractor.setWebsite(pConstructionContractor.getWebsite());
        constructionContractor.setNote(pConstructionContractor.getNote());
        return constructionContractorRepository.save(constructionContractor);
    };

    @Override
    public ConstructionContractor updateConstructionContractor(Integer addressId, Integer id,
            ConstructionContractor pConstructionContractor) {
        ConstructionContractor constructionContractor = constructionContractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Construction Contractor is not found"));
        AddressMap addressMap = addressMapRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("The Address Map is not found"));

        constructionContractor.setName(pConstructionContractor.getName());
        constructionContractor.setDescription(pConstructionContractor.getDescription());
        constructionContractor.setAddress(addressMap);
        constructionContractor.setPhone(pConstructionContractor.getPhone());
        constructionContractor.setPhone2(pConstructionContractor.getPhone2());
        constructionContractor.setFax(pConstructionContractor.getFax());
        constructionContractor.setEmail(pConstructionContractor.getEmail());
        constructionContractor.setWebsite(pConstructionContractor.getWebsite());
        constructionContractor.setNote(pConstructionContractor.getNote());
        return constructionContractorRepository.save(constructionContractor);
    };

    @Override
    public void deleteConstructionContractor(Integer id) {
        constructionContractorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Construction Contractor is not found"));
        constructionContractorRepository.deleteById(id);
    };
}
