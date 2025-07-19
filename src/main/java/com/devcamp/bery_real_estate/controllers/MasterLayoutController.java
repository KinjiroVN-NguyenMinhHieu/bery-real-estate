package com.devcamp.bery_real_estate.controllers;
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

import com.devcamp.bery_real_estate.dtos.MasterLayoutDto;
import com.devcamp.bery_real_estate.entities.MasterLayout;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IMasterLayoutService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class MasterLayoutController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IMasterLayoutService masterLayoutService;

    /**
     * get all
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/master-layouts")
    public ResponseEntity<Page<MasterLayoutDto>> getAllMasterLayouts(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<MasterLayoutDto> masterLayoutDtos = masterLayoutService
                    .getAllMasterLayouts(page, size)
                    .map(masterLayout -> modelMapper.map(masterLayout,
                            MasterLayoutDto.class));
            return new ResponseEntity<>(masterLayoutDtos, HttpStatus.OK);
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
    @GetMapping("/master-layouts/{layoutId}")
    public ResponseEntity<Object> getMasterLayoutById(
            @PathVariable(name = "layoutId", required = true) Integer id) {
        try {
            MasterLayout masterLayout = masterLayoutService
                    .getMasterLayoutById(id);
            // convert sang dto
            MasterLayoutDto masterLayoutDto = modelMapper.map(masterLayout,
                    MasterLayoutDto.class);
            return new ResponseEntity<>(masterLayoutDto, HttpStatus.OK);
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
     * @param projectId
     * @param pMasterLayoutDto
     * @return
     */
    @PostMapping("/projects/{projectId}/master-layouts")
    public ResponseEntity<Object> createMasterLayout(@PathVariable(required = true) Integer projectId,
            @Valid @RequestBody MasterLayoutDto pMasterLayoutDto) {
        try {
            // convert sang entity
            MasterLayout pMasterLayout = modelMapper.map(pMasterLayoutDto,
                    MasterLayout.class);
            // xử lý dữ liệu
            MasterLayout masterLayout = masterLayoutService
                    .createMasterLayout(projectId, pMasterLayout);
            // convert sang dto
            MasterLayoutDto masterLayoutDto = modelMapper.map(masterLayout,
                    MasterLayoutDto.class);
            return new ResponseEntity<>(masterLayoutDto, HttpStatus.CREATED);
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
     * @param projectId
     * @param id
     * @param pMasterLayoutDto
     * @return
     */
    @PutMapping("/projects/{projectId}/master-layouts/{layoutId}")
    public ResponseEntity<Object> updateMasterLayout(@PathVariable(required = true) Integer projectId,
            @PathVariable(name = "layoutId", required = true) Integer id,
            @Valid @RequestBody MasterLayoutDto pMasterLayoutDto) {
        try {
            // convert sang entity
            MasterLayout pMasterLayout = modelMapper.map(pMasterLayoutDto,
                    MasterLayout.class);
            // xử lý dữ liệu
            MasterLayout masterLayout = masterLayoutService
                    .updateMasterLayout(projectId, id, pMasterLayout);
            // convert sang dto
            MasterLayoutDto masterLayoutDto = modelMapper.map(masterLayout,
                    MasterLayoutDto.class);
            return new ResponseEntity<>(masterLayoutDto, HttpStatus.OK);
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
    @DeleteMapping("/master-layouts/{layoutId}")
    public ResponseEntity<Object> deleteAddressMap(@PathVariable(name = "layoutId") Integer id) {
        try {
            masterLayoutService.deleteMasterLayout(id);
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
