package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.Utility;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IUtilityRepository;
import com.devcamp.bery_real_estate.services.IUtilityService;

@Service
public class UtilityServiceImpl implements IUtilityService {
    @Autowired
    private IUtilityRepository utilityRepository;

    @Override
    public List<Utility> getListUtilities() {
        return utilityRepository.findAll();
    };

    @Override
    public Page<Utility> getAllUtilities(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return utilityRepository.findAll(pageable);
    };

    @Override
    public Utility getUtilityById(Integer id) {
        return utilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utility is not found"));
    };

    @Override
    public Utility createUtility(Utility pUtility) {
        Utility utility = new Utility();
        utility.setName(pUtility.getName());
        utility.setDescription(pUtility.getDescription());
        utility.setPhoto(pUtility.getPhoto());
        return utilityRepository.save(utility);
    };

    @Override
    public Utility updateUtility(Integer id, Utility pUtility) {
        Utility utility = utilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utility is not found"));
        utility.setName(pUtility.getName());
        utility.setDescription(pUtility.getDescription());
        utility.setPhoto(pUtility.getPhoto());
        return utilityRepository.save(utility);
    };

    @Override
    public void deleteUtility(Integer id) {
        utilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utility is not found"));
        utilityRepository.deleteById(id);
    };
}
