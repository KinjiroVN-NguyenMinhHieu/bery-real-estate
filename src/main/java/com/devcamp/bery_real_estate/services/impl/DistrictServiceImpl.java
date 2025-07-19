package com.devcamp.bery_real_estate.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;
import com.devcamp.bery_real_estate.repositories.IDistrictRepository;
import com.devcamp.bery_real_estate.repositories.IProvinceRepository;
import com.devcamp.bery_real_estate.services.IDistrictService;

@Service
public class DistrictServiceImpl implements IDistrictService {
    @Autowired
    private IDistrictRepository districtRepository;
    @Autowired
    private IProvinceRepository provinceRepository;

    @Override
    public List<District> getAllDistricts() {
        return districtRepository.findAllByOrderByProvinceAscPrefixAscIdAscNameAsc();
    }

    @Override
    public Set<Ward> getWardsByDistrictId(Integer id) {
        Optional<District> optinalDistrict = districtRepository.findById(id);
        if (optinalDistrict.isPresent()) {
            District district = optinalDistrict.get();
            return district.getWards();
        } else {
            throw new ResourceNotFoundException("The district is not found");
        }
    }

    @Override
    public Set<Street> getStreetsByDistrictId(Integer id) {
        Optional<District> optinalDistrict = districtRepository.findById(id);
        if (optinalDistrict.isPresent()) {
            District district = optinalDistrict.get();
            return district.getStreets();
        } else {
            throw new ResourceNotFoundException("The district is not found");
        }
    }

    @Override
    public Set<Project> getProjectsByDistrictId(Integer id) {
        Optional<District> optinalDistrict = districtRepository.findById(id);
        if (optinalDistrict.isPresent()) {
            District district = optinalDistrict.get();
            return district.getProjects();
        } else {
            throw new ResourceNotFoundException("The district is not found");
        }
    }

    @Override
    public District getDistrictById(Integer id) {
        Optional<District> optionalDistrict = districtRepository.findById(id);

        if (optionalDistrict.isPresent()) {
            return optionalDistrict.get();
        } else {
            throw new ResourceNotFoundException("The district is not found");
        }
    }

    @Override
    public District createDistrict(District pDistrict) {
        Optional<District> optinalDistrict = districtRepository.findById(pDistrict.getId());
        if (optinalDistrict.isPresent()) {
            throw new DuplicateKeyException("The district has existed");
        } else {
            Optional<Province> optionalProvince = provinceRepository.findById(pDistrict.getProvince().getId());
            if (optionalProvince.isPresent()) {
                pDistrict.setProvince(optionalProvince.get());
                pDistrict.setCreatedAt(new Date());
                pDistrict.setUpdatedAt(null);
                return districtRepository.save(pDistrict);
            } else {
                throw new ResourceNotFoundException("The corresponding province/ city is not found");
            }
        }
    }

    @Override
    public District updateDistrict(Integer id, District pDistrict) {
        //check huyện có tồn tại ko
        Optional<District> optionalDistrict = districtRepository.findById(id);

        if (optionalDistrict.isPresent()) {
            //check tỉnh có tồn tại ko
            Optional<Province> optionalProvince = provinceRepository.findById(pDistrict.getProvince().getId());
            if (optionalProvince.isPresent()) {
                District district = optionalDistrict.get();
                district.setName(pDistrict.getName());
                district.setPrefix(pDistrict.getPrefix());
                district.setProvince(optionalProvince.get());
                district.setUpdatedAt(new Date());
                return districtRepository.save(district);
            } else {
                throw new ResourceNotFoundException("The corresponding province/ city is not found");
            }
        } else {
            throw new ResourceNotFoundException("The district is not found");
        }
    }

    @Override
    public void deleteDistrict(Integer id) {
        //check huyện có tồn tại ko
        Optional<District> optionalDistrict = districtRepository.findById(id);
        if (optionalDistrict.isPresent()) {
            districtRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("The district is not found");
        }
    }
}
