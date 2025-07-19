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
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.repositories.IDistrictRepository;
import com.devcamp.bery_real_estate.repositories.IProvinceRepository;
import com.devcamp.bery_real_estate.repositories.IStreetRepository;
import com.devcamp.bery_real_estate.services.IStreetService;

@Service
public class StreetServiceImpl implements IStreetService {
    @Autowired
    private IStreetRepository streetRepository;
    @Autowired
    private IDistrictRepository districtRepository;
    @Autowired
    private IProvinceRepository provinceRepository;

    @Override
    public List<Street> getAllStreets() {
        return streetRepository.findAll();
    }

    @Override
    public Street getStreetById(Integer id) {
        Optional<Street> optionalStreet = streetRepository.findById(id);

        if (optionalStreet.isPresent()) {
            return optionalStreet.get();
        } else {
            throw new ResourceNotFoundException("Not found in the street");
        }
    }

    @Override
    public Street createStreet(Street pStreet) {
        // check xã phường tồn tại
        Optional<Street> optionalStreet = streetRepository.findById(pStreet.getId());

        if (optionalStreet.isPresent()) {
            throw new DuplicateKeyException("Street existed");
        } else {
            // check huyện
            Optional<District> optionalDistrict = districtRepository.findById(pStreet.getDistrict().getId());
            // check tỉnh
            Province province = provinceRepository.findById(pStreet.getProvince().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("The corresponding province cannot be found"));
            if (optionalDistrict.isPresent()) {
                pStreet.setDistrict(optionalDistrict.get());
                pStreet.setProvince(province);
                pStreet.setCreatedAt(new Date());
                pStreet.setUpdatedAt(null);
                return streetRepository.save(pStreet);
            } else {
                throw new ResourceNotFoundException("The corresponding district cannot be found");
            }
        }
    }

    @Override
    public Street updateStreet(Integer id, Street pStreet) {
        // check xã có tồn tại ko
        Optional<Street> optionalStreet = streetRepository.findById(id);

        if (optionalStreet.isPresent()) {
            // check huyện có tồn tại ko
            Optional<District> optionalDistrict = districtRepository.findById(pStreet.getDistrict().getId());
            // check tỉnh
            Province province = provinceRepository.findById(pStreet.getProvince().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("The corresponding province cannot be found"));
            if (optionalDistrict.isPresent()) {
                Street street = optionalStreet.get();
                street.setName(pStreet.getName());
                street.setPrefix(pStreet.getPrefix());
                street.setDistrict(optionalDistrict.get());
                street.setProvince(province);
                street.setUpdatedAt(new Date());
                return streetRepository.save(street);
            } else {
                throw new ResourceNotFoundException("The corresponding district cannot be found");
            }
        } else {
            throw new ResourceNotFoundException("Not found in the street");
        }
    }

    @Override
    public void deleteStreet(Integer id) {
        streetRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found in the street"));
        streetRepository.deleteById(id);
    }
}
