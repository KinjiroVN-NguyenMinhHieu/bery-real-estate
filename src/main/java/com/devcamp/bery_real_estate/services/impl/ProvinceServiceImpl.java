package com.devcamp.bery_real_estate.services.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.repositories.IProvinceRepository;
import com.devcamp.bery_real_estate.services.IProvinceService;

@Service
public class ProvinceServiceImpl implements IProvinceService {
    @Autowired
    private IProvinceRepository provinceRepository;

    @Override
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public Set<District> getDistrictsByProvinceId(Integer id) {
        Optional<Province> optionalProvince = provinceRepository.findById(id);

        if (optionalProvince.isPresent()) {
            Province province = optionalProvince.get();

            return province.getDistricts();
        } else {
            throw new ResourceNotFoundException("No provincial/ city found");
        }
    }

    @Override
    public List<Province> getProvincesPanigation(String page,String size) {
        //tạo obj pageable chứa thông tin phân trang
        Pageable pageable = PageRequest.of(Integer.parseInt(page), Integer.parseInt(size));
        //tìm theo thông tin phân trang(findAll(pageable) trả về 1 pageable)
        return provinceRepository.findAll(pageable).getContent();
    }

    @Override
    public Province getProvinceById(Integer id) {
        //check tỉnh có tồn tại ko
        Optional<Province> optionalProvince = provinceRepository.findById(id);

        if (optionalProvince.isPresent()) {
            return optionalProvince.get();
        } else {
            throw new ResourceNotFoundException("No provincial/ city found");
        }
    }

    @Override
    public Province createProvince(Province pProvince) {
        Optional<Province> optionalProvince = provinceRepository.findById(pProvince.getId());

        if (optionalProvince.isPresent()) {
            throw new DuplicateKeyException("Province/ city existed");
        } else {
            pProvince.setCreatedAt(new Date());
            pProvince.setUpdatedAt(null);
            return provinceRepository.save(pProvince);
        }
    }

    @Override
    public Province updateProvine(Integer id, Province pProvince) {
        //check tỉnh có tồn tại ko
        Optional<Province> optionalProvince = provinceRepository.findById(id);

        if (optionalProvince.isPresent()) {
            Province province = optionalProvince.get();
            province.setName(pProvince.getName());
            province.setCode(pProvince.getCode());
            province.setUpdatedAt(new Date());
            return provinceRepository.save(province);
        } else {
            throw new ResourceNotFoundException("No provincial/ city found");
        }
    }

    @Override
    public void deleteProvince(Integer id) {
        //check tỉnh có tồn tại ko
        Optional<Province> optionalProvince = provinceRepository.findById(id);

        if (optionalProvince.isPresent()) {
            provinceRepository.deleteById(id);
        } else {
            throw new ResourceNotFoundException("No provincial/ city found");
        }
    }
}
