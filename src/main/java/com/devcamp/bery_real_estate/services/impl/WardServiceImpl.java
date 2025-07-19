package com.devcamp.bery_real_estate.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.models.Ward;
import com.devcamp.bery_real_estate.repositories.IDistrictRepository;
import com.devcamp.bery_real_estate.repositories.IProvinceRepository;
import com.devcamp.bery_real_estate.repositories.IWardRepository;
import com.devcamp.bery_real_estate.services.IWardService;

@Service
public class WardServiceImpl implements IWardService {
    @Autowired
    private IWardRepository wardRepository;
    @Autowired
    private IDistrictRepository districtRepository;
    @Autowired
    private IProvinceRepository provinceRepository;

    @Override
    public List<Ward> getAllWards() {
        return wardRepository.findAll();
    }

    @Override
    public Ward getWardById(Integer id) {
        Optional<Ward> optionalWard = wardRepository.findById(id);

        if (optionalWard.isPresent()) {
            return optionalWard.get();
        } else {
            throw new ResourceNotFoundException("Not found in the commune/ ward");
        }
    }

    @Override
    public Ward createWard(Ward pWard) {
        // check xã phường tồn tại
        Optional<Ward> optionalWard = wardRepository.findById(pWard.getId());

        if (optionalWard.isPresent()) {
            throw new DuplicateKeyException("Commune/ ward existed");
        } else {
            // check huyện
            Optional<District> optionalDistrict = districtRepository.findById(pWard.getDistrict().getId());
            // check tỉnh
            Province province = provinceRepository.findById(pWard.getProvince().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("The corresponding province cannot be found"));
            if (optionalDistrict.isPresent()) {
                pWard.setDistrict(optionalDistrict.get());
                pWard.setProvince(province);
                pWard.setCreatedAt(new Date());
                pWard.setUpdatedAt(null);
                return wardRepository.save(pWard);
            } else {
                throw new ResourceNotFoundException("The corresponding district cannot be found");
            }
        }
    }

    @Override
    public Ward updateWard(Integer id, Ward pWard) {
        // check xã có tồn tại ko
        Optional<Ward> optionalWard = wardRepository.findById(id);

        if (optionalWard.isPresent()) {
            // check huyện có tồn tại ko
            Optional<District> optionalDistrict = districtRepository.findById(pWard.getDistrict().getId());
            // check tỉnh
            Province province = provinceRepository.findById(pWard.getProvince().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("The corresponding province cannot be found"));
            if (optionalDistrict.isPresent()) {
                Ward ward = optionalWard.get();
                ward.setName(pWard.getName());
                ward.setPrefix(pWard.getPrefix());
                ward.setDistrict(optionalDistrict.get());
                ward.setProvince(province);
                ward.setUpdatedAt(new Date());
                return wardRepository.save(ward);
            } else {
                throw new ResourceNotFoundException("The corresponding district cannot be found");
            }
        } else {
            throw new ResourceNotFoundException("Not found in the commune/ ward");
        }
    }

    @Override
    public void deleteWard(Integer id) {
        wardRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found in the commune/ ward"));
        wardRepository.deleteById(id);
    }
}
