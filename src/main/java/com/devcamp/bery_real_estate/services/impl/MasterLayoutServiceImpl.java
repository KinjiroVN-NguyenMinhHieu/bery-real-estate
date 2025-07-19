package com.devcamp.bery_real_estate.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.MasterLayout;
import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IMasterLayoutRepository;
import com.devcamp.bery_real_estate.repositories.IProjectRepository;
import com.devcamp.bery_real_estate.services.IMasterLayoutService;

@Service
public class MasterLayoutServiceImpl implements IMasterLayoutService {
    @Autowired
    private IMasterLayoutRepository masterLayoutRepository;
    @Autowired
    private IProjectRepository projectRepository;

    @Override
    public Page<MasterLayout> getAllMasterLayouts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return masterLayoutRepository.findAll(pageable);
    };

    @Override
    public MasterLayout getMasterLayoutById(Integer id) {
        return masterLayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Master Layout is not found"));
    };

    @Override
    public MasterLayout createMasterLayout(Integer projectId, MasterLayout pMasterLayout) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("The Project is not found"));

        MasterLayout masterLayout = new MasterLayout();
        masterLayout.setName(pMasterLayout.getName());
        masterLayout.setDescription(pMasterLayout.getDescription());
        masterLayout.setProject(project);
        masterLayout.setAcreage(pMasterLayout.getAcreage());
        masterLayout.setApartmentList(pMasterLayout.getApartmentList());
        masterLayout.setPhoto(pMasterLayout.getPhoto());
        masterLayout.setDateCreate(new Date());
        return masterLayoutRepository.save(masterLayout);
    };

    @Override
    public MasterLayout updateMasterLayout(Integer projectId, Integer id, MasterLayout pMasterLayout) {
        MasterLayout masterLayout = masterLayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Master Layout is not found"));
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("The Project is not found"));

        masterLayout.setName(pMasterLayout.getName());
        masterLayout.setDescription(pMasterLayout.getDescription());
        masterLayout.setProject(project);
        masterLayout.setAcreage(pMasterLayout.getAcreage());
        masterLayout.setApartmentList(pMasterLayout.getApartmentList());
        masterLayout.setPhoto(pMasterLayout.getPhoto());
        masterLayout.setDateUpdate((new Date()));
        return masterLayoutRepository.save(masterLayout);
    };

    @Override
    public void deleteMasterLayout(Integer id) {
        masterLayoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Master Layout is not found"));
        masterLayoutRepository.deleteById(id);
    };
}
