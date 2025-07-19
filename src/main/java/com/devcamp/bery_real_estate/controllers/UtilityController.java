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

import com.devcamp.bery_real_estate.dtos.UtilityDto;
import com.devcamp.bery_real_estate.entities.Utility;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IUtilityService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class UtilityController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IUtilityService utilityService;

    /**
     * get list
     * @return
     */
    @GetMapping("utilities/all")
    public ResponseEntity<List<UtilityDto>> getListUtilities() {
        try {
            List<UtilityDto> utilityDtos = utilityService
                    .getListUtilities().stream()
                    .map(utility -> modelMapper.map(utility, UtilityDto.class))
                    .toList();
            return new ResponseEntity<>(utilityDtos, HttpStatus.OK);
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
    @GetMapping("utilities")
    public ResponseEntity<Page<UtilityDto>> getAllUtilities(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<UtilityDto> utilityDtos = utilityService
                    .getAllUtilities(page, size)
                    .map(utility -> modelMapper.map(utility, UtilityDto.class));
            return new ResponseEntity<>(utilityDtos, HttpStatus.OK);
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
    @GetMapping("utilities/{utilityId}")
    public ResponseEntity<Object> getUtilityById(
            @PathVariable(name = "utilityId", required = true) Integer id) {
        try {
            Utility utility = utilityService.getUtilityById(id);
            // convert sang dto
            UtilityDto utilityDto = modelMapper.map(utility, UtilityDto.class);
            return new ResponseEntity<>(utilityDto, HttpStatus.OK);
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
     * @param pUtilityDto
     * @return
     */
    @PostMapping("/utilities")
    public ResponseEntity<Object> createUtility(@Valid @RequestBody UtilityDto pUtilityDto) {
        try {
            // convert sang entity
            Utility pUtility = modelMapper.map(pUtilityDto,
                    Utility.class);
            // xử lý dữ liệu
            Utility utility = utilityService.createUtility(pUtility);
            // convert sang dto
            UtilityDto utilityDto = modelMapper.map(utility, UtilityDto.class);
            return new ResponseEntity<>(utilityDto, HttpStatus.CREATED);
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
     * @param id
     * @param pUtilityDto
     * @return
     */
    @PutMapping("/utilities/{utilityId}")
    public ResponseEntity<Object> updateUtility(@PathVariable(name = "utilityId", required = true) Integer id,
            @Valid @RequestBody UtilityDto pUtilityDto) {
        try {
            // convert sang entity
            Utility pUtility = modelMapper.map(pUtilityDto, Utility.class);
            // xử lý dữ liệu
            Utility utility = utilityService.updateUtility(id, pUtility);
            // convert sang dto
            UtilityDto utilityDto = modelMapper.map(utility,
                    UtilityDto.class);
            return new ResponseEntity<>(utilityDto, HttpStatus.OK);
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
    @DeleteMapping("/utilities/{utilityId}")
    public ResponseEntity<Object> deleteUtility(@PathVariable(name = "utilityId") Integer id) {
        try {
            utilityService.deleteUtility(id);
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
