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

import com.devcamp.bery_real_estate.dtos.DesignUnitDto;
import com.devcamp.bery_real_estate.entities.DesignUnit;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IDesignUnitService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class DesignUnitController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IDesignUnitService designUnitService;

    /**
     * get list
     * @return
     */
    @GetMapping("/design-units/all")
    public ResponseEntity<List<DesignUnitDto>> getListDesignUnits() {
        try {
            List<DesignUnitDto> designUnitDtos = designUnitService
                    .getListDesignUnits().stream()
                    .map(designUnit -> modelMapper.map(designUnit,
                            DesignUnitDto.class)).toList();
            return new ResponseEntity<>(designUnitDtos, HttpStatus.OK);
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
    @GetMapping("/design-units")
    public ResponseEntity<Page<DesignUnitDto>> getAllDesignUnits(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<DesignUnitDto> designUnitDtos = designUnitService
                    .getAllDesignUnits(page, size)
                    .map(designUnit -> modelMapper.map(designUnit,
                            DesignUnitDto.class));
            return new ResponseEntity<>(designUnitDtos, HttpStatus.OK);
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
    @GetMapping("/design-units/{designId}")
    public ResponseEntity<Object> getDesignUnitById(
            @PathVariable(name = "designId", required = true) Integer id) {
        try {
            DesignUnit designUnit = designUnitService
                    .getDesignUnitById(id);
            // convert sang dto
            DesignUnitDto designUnitDto = modelMapper.map(designUnit,
                    DesignUnitDto.class);
            return new ResponseEntity<>(designUnitDto, HttpStatus.OK);
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
     * @param pDesignUnitDto
     * @return
     */
    @PostMapping("/address-maps/{addressId}/design-units")
    public ResponseEntity<Object> createDesignUnit(@PathVariable(required = true) Integer addressId,
            @Valid @RequestBody DesignUnitDto pDesignUnitDto) {
        try {
            // convert sang entity
            DesignUnit pDesignUnit = modelMapper.map(pDesignUnitDto,
                    DesignUnit.class);
            // xử lý dữ liệu
            DesignUnit designUnit = designUnitService
                    .createDesignUnit(addressId, pDesignUnit);
            // convert sang dto
            DesignUnitDto designUnitDto = modelMapper.map(designUnit,
                    DesignUnitDto.class);
            return new ResponseEntity<>(designUnitDto, HttpStatus.CREATED);
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
     * @param pDesignUnitDto
     * @return
     */
    @PutMapping("/address-maps/{addressId}/design-units/{designId}")
    public ResponseEntity<Object> updateDesignUnit(@PathVariable(required = true) Integer addressId,
            @PathVariable(name = "designId", required = true) Integer id,
            @Valid @RequestBody DesignUnitDto pDesignUnitDto) {
        try {
            // convert sang entity
            DesignUnit pDesignUnit = modelMapper.map(pDesignUnitDto,
                    DesignUnit.class);
            // xử lý dữ liệu
            DesignUnit designUnit = designUnitService
                    .updateDesignUnit(addressId, id, pDesignUnit);
            // convert sang dto
            DesignUnitDto designUnitDto = modelMapper.map(designUnit,
                    DesignUnitDto.class);
            return new ResponseEntity<>(designUnitDto, HttpStatus.OK);
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
    @DeleteMapping("/design-units/{designId}")
    public ResponseEntity<Object> deleteAddressMap(@PathVariable(name = "designId") Integer id) {
        try {
            designUnitService.deleteDesignUnit(id);
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
