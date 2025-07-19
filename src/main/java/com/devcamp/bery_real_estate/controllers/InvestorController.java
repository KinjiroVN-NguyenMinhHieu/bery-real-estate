package com.devcamp.bery_real_estate.controllers;

import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devcamp.bery_real_estate.dtos.InvestorDto;
import com.devcamp.bery_real_estate.entities.Investor;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IInvestorService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class InvestorController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IInvestorService investorService;

    /**
     * get list
     * @return
     */
    @GetMapping("/investor/all")
    public ResponseEntity<List<InvestorDto>> getListInvestors() {
        try {
            List<InvestorDto> investorDtos = investorService
                    .getListInvestors().stream()
                    .map(investor -> modelMapper.map(investor,
                            InvestorDto.class)).toList();
            return new ResponseEntity<>(investorDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/investor")
    public ResponseEntity<Page<InvestorDto>> getAllInvestors(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<InvestorDto> investorDtos = investorService
                    .getAllInvestors(page, size)
                    .map(investor -> modelMapper.map(investor,
                            InvestorDto.class));
            return new ResponseEntity<>(investorDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by id
     * @param id
     * @return
     */
    @GetMapping("/investor/{investorId}")
    public ResponseEntity<Object> getInvestorById(
            @PathVariable(name = "investorId", required = true) Integer id) {
        try {
            Investor investor = investorService
                    .getInvestorById(id);
            // convert sang dto
            InvestorDto investorDto = modelMapper.map(investor,
                    InvestorDto.class);
            return new ResponseEntity<>(investorDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * add
     * @param addressId
     * @param pInvestorDto
     * @return
     */
    @PostMapping("/address-maps/{addressId}/investor")
    public ResponseEntity<Object> createInvestor(@PathVariable(required = true) Integer addressId,
            @Valid @RequestBody InvestorDto pInvestorDto) {
        try {
            // convert sang entity
            Investor pInvestor = modelMapper.map(pInvestorDto,
                    Investor.class);
            // xử lý dữ liệu
            Investor investor = investorService
                    .createInvestor(addressId, pInvestor);
            // convert sang dto
            InvestorDto investorDto = modelMapper.map(investor,
                    InvestorDto.class);
            return new ResponseEntity<>(investorDto, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param addressId
     * @param id
     * @param pInvestorDto
     * @return
     */
    @PutMapping("/address-maps/{addressId}/investor/{investorId}")
    public ResponseEntity<Object> updateInvestor(@PathVariable(required = true) Integer addressId,
            @PathVariable(name = "investorId", required = true) Integer id,
            @Valid @RequestBody InvestorDto pInvestorDto) {
        try {
            // convert sang entity
            Investor pInvestor = modelMapper.map(pInvestorDto,
                    Investor.class);
            // xử lý dữ liệu
            Investor investor = investorService
                    .updateInvestor(addressId, id, pInvestor);
            // convert sang dto
            InvestorDto investorDto = modelMapper.map(investor,
                    InvestorDto.class);
            return new ResponseEntity<>(investorDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
			System.err.println(e.getMessage());
			return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
		} catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete
     * @param id
     * @return
     */
    @DeleteMapping("/investor/{investorId}")
    public ResponseEntity<Object> deleteAddressMap(@PathVariable(name = "investorId") Integer id) {
        try {
            investorService.deleteInvestor(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
