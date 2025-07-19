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

import com.devcamp.bery_real_estate.dtos.ConstructionContractorDto;
import com.devcamp.bery_real_estate.entities.ConstructionContractor;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IConstructionContractorService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class ConstructionContractorController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IConstructionContractorService constructionContractorService;

    /**
     * get list
     * @return
     */
    @GetMapping("/construction-contractors/all")
    public ResponseEntity<List<ConstructionContractorDto>> getListConstructionContractors() {
        try {
            List<ConstructionContractorDto> constructionContractorDtos = constructionContractorService
                    .getListConstructionContractors().stream()
                    .map(constructionContractor -> modelMapper.map(constructionContractor,
                            ConstructionContractorDto.class)).toList();
            return new ResponseEntity<>(constructionContractorDtos, HttpStatus.OK);
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
    @GetMapping("/construction-contractors")
    public ResponseEntity<Page<ConstructionContractorDto>> getAllConstructionContractors(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<ConstructionContractorDto> constructionContractorDtos = constructionContractorService
                    .getAllConstructionContractors(page, size)
                    .map(constructionContractor -> modelMapper.map(constructionContractor,
                            ConstructionContractorDto.class));
            return new ResponseEntity<>(constructionContractorDtos, HttpStatus.OK);
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
    @GetMapping("/construction-contractors/{contractorId}")
    public ResponseEntity<Object> getConstructionContractorById(
            @PathVariable(name = "contractorId", required = true) Integer id) {
        try {
            ConstructionContractor constructionContractor = constructionContractorService
                    .getConstructionContractorById(id);
            // convert sang dto
            ConstructionContractorDto constructionContractorDto = modelMapper.map(constructionContractor,
                    ConstructionContractorDto.class);
            return new ResponseEntity<>(constructionContractorDto, HttpStatus.OK);
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
     * @param pConstructionContractorDto
     * @return
     */
    @PostMapping("/address-maps/{addressId}/construction-contractors")
    public ResponseEntity<Object> createConstructionContractor(@PathVariable(required = true) Integer addressId,
            @Valid @RequestBody ConstructionContractorDto pConstructionContractorDto) {
        try {
            // convert sang entity
            ConstructionContractor pConstructionContractor = modelMapper.map(pConstructionContractorDto,
                    ConstructionContractor.class);
            // xử lý dữ liệu
            ConstructionContractor constructionContractor = constructionContractorService
                    .createConstructionContractor(addressId, pConstructionContractor);
            // convert sang dto
            ConstructionContractorDto constructionContractorDto = modelMapper.map(constructionContractor,
                    ConstructionContractorDto.class);
            return new ResponseEntity<>(constructionContractorDto, HttpStatus.CREATED);
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
     * @param pConstructionContractorDto
     * @return
     */
    @PutMapping("/address-maps/{addressId}/construction-contractors/{contractorId}")
    public ResponseEntity<Object> updateConstructionContractor(@PathVariable(required = true) Integer addressId,
            @PathVariable(name = "contractorId", required = true) Integer id,
            @Valid @RequestBody ConstructionContractorDto pConstructionContractorDto) {
        try {
            // convert sang entity
            ConstructionContractor pConstructionContractor = modelMapper.map(pConstructionContractorDto,
                    ConstructionContractor.class);
            // xử lý dữ liệu
            ConstructionContractor constructionContractor = constructionContractorService
                    .updateConstructionContractor(addressId, id, pConstructionContractor);
            // convert sang dto
            ConstructionContractorDto constructionContractorDto = modelMapper.map(constructionContractor,
                    ConstructionContractorDto.class);
            return new ResponseEntity<>(constructionContractorDto, HttpStatus.OK);
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
    @DeleteMapping("/construction-contractors/{contractorId}")
    public ResponseEntity<Object> deleteAddressMap(@PathVariable(name = "contractorId") Integer id) {
        try {
            constructionContractorService.deleteConstructionContractor(id);
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
