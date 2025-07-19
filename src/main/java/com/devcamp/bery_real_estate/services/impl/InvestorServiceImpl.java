package com.devcamp.bery_real_estate.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.devcamp.bery_real_estate.entities.AddressMap;
import com.devcamp.bery_real_estate.entities.Investor;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IAddressMapRepository;
import com.devcamp.bery_real_estate.repositories.IInvestorRepository;
import com.devcamp.bery_real_estate.services.IInvestorService;

@Service
public class InvestorServiceImpl implements IInvestorService {
    @Autowired
    private IInvestorRepository investorRepository;
    @Autowired
    private IAddressMapRepository addressMapRepository;

    @Override
    public List<Investor> getListInvestors() {
        return investorRepository.findAll();
    };

    @Override
    public Page<Investor> getAllInvestors(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return investorRepository.findAll(pageable);
    };

    @Override
    public Investor getInvestorById(Integer id) {
        return investorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Investor is not found"));
    };

    @Override
    public Investor createInvestor(Integer addressId,
            Investor pInvestor) {
        AddressMap addressMap = addressMapRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("The Address Map is not found"));

        Investor investor = new Investor();
        investor.setName(pInvestor.getName());
        investor.setDescription(pInvestor.getDescription());
        investor.setAddress(addressMap);
        investor.setPhone(pInvestor.getPhone());
        investor.setPhone2(pInvestor.getPhone2());
        investor.setFax(pInvestor.getFax());
        investor.setEmail(pInvestor.getEmail());
        investor.setWebsite(pInvestor.getWebsite());
        investor.setNote(pInvestor.getNote());
        return investorRepository.save(investor);
    };

    @Override
    public Investor updateInvestor(Integer addressId, Integer id,
            Investor pInvestor) {
        Investor investor = investorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Investor is not found"));
        AddressMap addressMap = addressMapRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("The Address Map is not found"));

        investor.setName(pInvestor.getName());
        investor.setDescription(pInvestor.getDescription());
        investor.setAddress(addressMap);
        investor.setPhone(pInvestor.getPhone());
        investor.setPhone2(pInvestor.getPhone2());
        investor.setFax(pInvestor.getFax());
        investor.setEmail(pInvestor.getEmail());
        investor.setWebsite(pInvestor.getWebsite());
        investor.setNote(pInvestor.getNote());
        return investorRepository.save(investor);
    };

    @Override
    public void deleteInvestor(Integer id) {
        investorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The Investor is not found"));
        investorRepository.deleteById(id);
    };
}
