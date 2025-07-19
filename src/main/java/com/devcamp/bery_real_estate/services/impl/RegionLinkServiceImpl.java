package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.RegionLink;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IRegionLinkRepository;
import com.devcamp.bery_real_estate.services.IRegionLinkService;

@Service
public class RegionLinkServiceImpl implements IRegionLinkService {
    @Autowired
    private IRegionLinkRepository regionLinkRepository;

    @Override
    public List<RegionLink> getListRegionLinks() {
        return regionLinkRepository.findAll();
    };

    @Override
    public Page<RegionLink> getAllRegionLinks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return regionLinkRepository.findAll(pageable);
    };

    @Override
    public RegionLink getRegionLinkById(Integer id) {
        return regionLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region Link is not found"));
    };

    @Override
    public RegionLink createRegionLink(RegionLink pRegionLink) {
        RegionLink regionLink = new RegionLink();
        regionLink.setName(pRegionLink.getName());
        regionLink.setDescription(pRegionLink.getDescription());
        regionLink.setPhoto(pRegionLink.getPhoto());
        regionLink.setAddress(pRegionLink.getAddress());
        regionLink.setLatitude(pRegionLink.getLatitude());
        regionLink.setLongitude(pRegionLink.getLongitude());
        return regionLinkRepository.save(regionLink);
    };

    @Override
    public RegionLink updateRegionLink(Integer id, RegionLink pRegionLink) {
        RegionLink regionLink = regionLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region Link is not found"));
        regionLink.setName(pRegionLink.getName());
        regionLink.setDescription(pRegionLink.getDescription());
        regionLink.setPhoto(pRegionLink.getPhoto());
        regionLink.setAddress(pRegionLink.getAddress());
        regionLink.setLatitude(pRegionLink.getLatitude());
        regionLink.setLongitude(pRegionLink.getLongitude());
        return regionLinkRepository.save(regionLink);
    };

    @Override
    public void deleteRegionLink(Integer id) {
        regionLinkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Region Link is not found"));
        regionLinkRepository.deleteById(id);
    };
}
