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

import com.devcamp.bery_real_estate.dtos.RegionLinkDto;
import com.devcamp.bery_real_estate.entities.RegionLink;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IRegionLinkService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class RegionLinkController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IRegionLinkService regionLinkService;

    /**
     * get list
     * @return
     */
    @GetMapping("region-links/all")
    public ResponseEntity<List<RegionLinkDto>> getListRegionLinks() {
        try {
            List<RegionLinkDto> regionLinkDtos = regionLinkService
                    .getListRegionLinks().stream()
                    .map(regionLink -> modelMapper.map(regionLink,
                            RegionLinkDto.class))
                    .toList();
            return new ResponseEntity<>(regionLinkDtos, HttpStatus.OK);
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
    @GetMapping("region-links")
    public ResponseEntity<Page<RegionLinkDto>> getAllRegionLinks(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<RegionLinkDto> regionLinkDtos = regionLinkService
                    .getAllRegionLinks(page, size)
                    .map(regionLink -> modelMapper.map(regionLink,
                            RegionLinkDto.class));
            return new ResponseEntity<>(regionLinkDtos, HttpStatus.OK);
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
    @GetMapping("region-links/{regionId}")
    public ResponseEntity<Object> getRegionLinkById(
            @PathVariable(name = "regionId", required = true) Integer id) {
        try {
            RegionLink regionLink = regionLinkService
                    .getRegionLinkById(id);
            // convert sang dto
            RegionLinkDto regionLinkDto = modelMapper.map(regionLink,
                    RegionLinkDto.class);
            return new ResponseEntity<>(regionLinkDto, HttpStatus.OK);
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
     * @param pRegionLinkDto
     * @return
     */
    @PostMapping("/region-links")
    public ResponseEntity<Object> createRegionLink(@Valid @RequestBody RegionLinkDto pRegionLinkDto) {
        try {
            // convert sang entity
            RegionLink pRegionLink = modelMapper.map(pRegionLinkDto,
                    RegionLink.class);
            // xử lý dữ liệu
            RegionLink regionLink = regionLinkService
                    .createRegionLink(pRegionLink);
            // convert sang dto
            RegionLinkDto regionLinkDto = modelMapper.map(regionLink,
                    RegionLinkDto.class);
            return new ResponseEntity<>(regionLinkDto, HttpStatus.CREATED);
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
     * @param pRegionLinkDto
     * @return
     */
    @PutMapping("/region-links/{regionId}")
    public ResponseEntity<Object> updateRegionLink(@PathVariable(name = "regionId", required = true) Integer id,
            @Valid @RequestBody RegionLinkDto pRegionLinkDto) {
        try {
            // convert sang entity
            RegionLink pRegionLink = modelMapper.map(pRegionLinkDto,
                    RegionLink.class);
            // xử lý dữ liệu
            RegionLink regionLink = regionLinkService
                    .updateRegionLink(id, pRegionLink);
            // convert sang dto
            RegionLinkDto regionLinkDto = modelMapper.map(regionLink,
                    RegionLinkDto.class);
            return new ResponseEntity<>(regionLinkDto, HttpStatus.OK);
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
    @DeleteMapping("/region-links/{regionId}")
    public ResponseEntity<Object> deleteRegionLink(@PathVariable(name = "regionId") Integer id) {
        try {
            regionLinkService.deleteRegionLink(id);
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
