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

import com.devcamp.bery_real_estate.dtos.AddressMapDto;
import com.devcamp.bery_real_estate.entities.AddressMap;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IAddressMapService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class AddressMapController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IAddressMapService addressMapService;

    /**
     * get list
     * @return
     */
    @GetMapping("/address-maps/all")
    public ResponseEntity<List<AddressMapDto>> getListAddressMaps() {
        try {
            List<AddressMapDto> addressMapDtos = addressMapService
                    .getListAddressMaps().stream()
                    .map(addressMap -> modelMapper.map(addressMap, AddressMapDto.class)).toList();
            return new ResponseEntity<>(addressMapDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get all
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/address-maps")
    public ResponseEntity<Page<AddressMapDto>> getAllAddressMaps(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<AddressMapDto> addressMapDtos = addressMapService
                    .getAllAddressMaps(page, size)
                    .map(addressMap -> modelMapper.map(addressMap, AddressMapDto.class));
            return new ResponseEntity<>(addressMapDtos, HttpStatus.OK);
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
    @GetMapping("/address-maps/{addressId}")
    public ResponseEntity<Object> getAddressMapById(@PathVariable(name = "addressId", required = true) Integer id) {
        try {
            AddressMap addressMap = addressMapService.getAddressMapById(id);
            // convert sang dto
            AddressMapDto addressMapDto = modelMapper.map(addressMap, AddressMapDto.class);
            return new ResponseEntity<>(addressMapDto, HttpStatus.OK);
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
     * @param pAddressMapDto
     * @return
     */
    @PostMapping("/address-maps")
    public ResponseEntity<Object> createAddressMap(@Valid @RequestBody AddressMapDto pAddressMapDto) {
        try {
            // convert sang entity
            AddressMap pAddressMap = modelMapper.map(pAddressMapDto, AddressMap.class);
            //xử lý dữ liệu
            AddressMap addressMap = addressMapService.createAddressMap(pAddressMap);
            // convert sang dto
            AddressMapDto addressMapDto = modelMapper.map(addressMap, AddressMapDto.class);
            return new ResponseEntity<>(addressMapDto, HttpStatus.CREATED);
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
     * @param pAddressMapDto
     * @return
     */
    @PutMapping("/address-maps/{addressId}")
    public ResponseEntity<Object> updateAddressMap(@PathVariable(name = "addressId") Integer id,
            @Valid @RequestBody AddressMapDto pAddressMapDto) {
        try {
            // convert sang entity
            AddressMap pAddressMap = modelMapper.map(pAddressMapDto, AddressMap.class);
            //xử lý dữ liệu
            AddressMap addressMap = addressMapService.updateAddressMap(id, pAddressMap);
            // convert sang dto
            AddressMapDto addressMapDto = modelMapper.map(addressMap, AddressMapDto.class);
            return new ResponseEntity<>(addressMapDto, HttpStatus.OK);
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
    @DeleteMapping("/address-maps/{addressId}")
    public ResponseEntity<Object> deleteAddressMap(@PathVariable(name = "addressId") Integer id) {
        try {
            addressMapService.deleteAddressMap(id);
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
