package com.devcamp.bery_real_estate.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devcamp.bery_real_estate.constants.EDirection;
import com.devcamp.bery_real_estate.constants.EFurnitureType;
import com.devcamp.bery_real_estate.constants.ERequest;
import com.devcamp.bery_real_estate.constants.EType;
import com.devcamp.bery_real_estate.dtos.RealEstateDto;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Photo;
import com.devcamp.bery_real_estate.services.IRealEstateService;


@RestController
@CrossOrigin
@RequestMapping("/")
public class RealEstateController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IRealEstateService realEstateService;

    /**
     * get list
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/real-estates/all")
    public ResponseEntity<List<RealEstateDto>> getListRealEstates() {
        try {
            List<RealEstateDto> realEstateDtos = realEstateService
                    .getListRealEstates().stream()
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class))
                    .toList();
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
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
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/real-estates")
    public ResponseEntity<Page<RealEstateDto>> getAllRealEstates(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<RealEstateDto> realEstateDtos = realEstateService
                    .getAllRealEstates(page, size)
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by status
     * @param page
     * @param size
     * @return
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/real-estates/pending")
    public ResponseEntity<Page<RealEstateDto>> getAllRealEstatesByStatus(@RequestParam(required = true) int page,
        @RequestParam(required = true) int size) {
        try {
            Page<RealEstateDto> realEstateDtos = realEstateService
                    .getAllRealEstatesByStatus(page, size)
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get publish
     * @return
     */
    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/real-estates/publish")
    public ResponseEntity<Object> getAllPublishedRealEstates() {
        try {
            List<RealEstateDto> realEstateDtos = realEstateService
                    .getAllPublishedRealEstates().stream()
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class))
                    .toList();
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/real-estates/count/pending")
    public ResponseEntity<Object> countAllRealEstates() {
        try {
            return ResponseEntity.ok(realEstateService.countPendingRealEstates());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/real-estates/count/percent")
    public ResponseEntity<Object> countPercentageRealEstatesApproved() {
        try {
            return ResponseEntity.ok(realEstateService.countPercentageRealEstatesApproved());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/real-estates/publish/limit")
    public ResponseEntity<Object> getLimitPublishedRealEstates(@RequestParam(defaultValue = "10") int size) {
        try {
            Page<RealEstateDto> realEstateDtos = realEstateService
                    .getLimitPublishedRealEstates(size)
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/real-estates/unpublish/limit")
    public ResponseEntity<Object> getLimitUnpublishedRealEstates(@RequestParam(defaultValue = "10") int size) {
        try {
            Page<RealEstateDto> realEstateDtos = realEstateService
                    .getLimitUnpublishedRealEstates(size)
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get all pagination
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/real-estates/pagination")
    public ResponseEntity<Page<RealEstateDto>> getAllRealEstatesWithPagination(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        try {
            Page<RealEstateDto> realEstateDtos = realEstateService
                    .getAllRealEstatesWithPagination(page, size)
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/real-estates/pagination/luxury")
    public ResponseEntity<Page<RealEstateDto>> getAllLuxuryRealEstatesWithPagination(
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        try {
            Page<RealEstateDto> realEstateDtos = realEstateService
                    .getAllLuxuryRealEstatesWithPagination(page, size)
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/real-estates/count/province")
    public ResponseEntity<List<Object[]>> countRealEstatesByProvince() {
        try {
            List<Object[]> countList = realEstateService.countRealEstatesByProvince();
            return new ResponseEntity<>(countList, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * search and filter
     * @param keyword
     * @param type
     * @param request
     * @param furnitureType
     * @param direction
     * @param minPrice
     * @param maxPrice
     * @param minAcreage
     * @param maxAcreage
     * @param provinceId
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/real-estates/pagination/search-and-filter")
    public ResponseEntity<Page<RealEstateDto>> searchRealEstatesByKeywordAndFilterWithPagination(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String type, // Nhận dạng chuỗi
            @RequestParam(required = false) String request, // Nhận dạng chuỗi
            @RequestParam(required = false) String furnitureType, // Nhận dạng chuỗi
            @RequestParam(required = false) String direction, // Nhận dạng chuỗi
            @RequestParam(required = false) Long minPrice,
            @RequestParam(required = false) Long maxPrice,
            @RequestParam(required = false) BigDecimal minAcreage,
            @RequestParam(required = false) BigDecimal maxAcreage,
            @RequestParam(required = false) Integer provinceId,
            @RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        try {
            // Chuyển string sang enum sử dụng valueOf
            EType enumType = (type != null) ? EType.valueOf(type) : null;
            ERequest enumRequest = (request != null) ? ERequest.valueOf(request) : null;
            EFurnitureType enumFurnitureType = (furnitureType != null) ? EFurnitureType.valueOf(furnitureType) : null;
            EDirection enumDirection = (direction != null) ? EDirection.valueOf(direction) : null;

            Page<RealEstate> realEstatesPage = realEstateService.searchRealEstatesByKeywordAndFilterWithPagination(
                    keyword, enumType, enumRequest, enumFurnitureType, enumDirection, minPrice, maxPrice, minAcreage,
                    maxAcreage, provinceId, page, size);

            Page<RealEstateDto> realEstateDtos = realEstatesPage
                    .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));

            return new ResponseEntity<>(realEstateDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by id
     */
    @GetMapping("/real-estates/{estateId}")
    public ResponseEntity<Object> getRealEstateById(
            @PathVariable(name = "estateId", required = true) Integer id) {
        try {
            RealEstate realEstate = realEstateService.getRealEstateById(id);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Creates a new real estate based on the provided data.
     *
     * @param pRealEstateDto The DTO containing the real estate information.
     * @param photoFiles      The photo file of the real estate.
     * @return A ResponseEntity containing the created real estate DTO if
     *         successful, or an error response if not.
     */
    @PostMapping("/real-estates")
    public ResponseEntity<Object> createRealEstate(
            @Valid @ModelAttribute RealEstateDto pRealEstateDto,
            @RequestParam("photoFiles") List<MultipartFile> photoFiles) {
        try {
            // Tạo mới và lưu 1 realestate để lấy id
            RealEstate savedRealEstate = realEstateService.createAndSaveNewRealEstate(pRealEstateDto);
            // gán id và 2 thuộc tính mặc định cho pRealEstateDto
            pRealEstateDto.setId(savedRealEstate.getId());
            pRealEstateDto.setStatus(savedRealEstate.getStatus());
            pRealEstateDto.setDeleted(savedRealEstate.isDeleted());
            pRealEstateDto.setCreatedAt(savedRealEstate.getCreatedAt());
            // Lưu ảnh và gán link ảnh trước
            List<Photo> photos = realEstateService.savePhotos(pRealEstateDto.getId(), photoFiles);
            pRealEstateDto.setPhotos(photos);
            // convert sang entity
            RealEstate pRealEstate = modelMapper.map(pRealEstateDto, RealEstate.class);
            // xử lý dữ liệu
            RealEstate realEstate = realEstateService.createRealEstate(pRealEstate);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Updates an existing real estate with the provided data.
     *
     * @param id             The ID of the real estate to be updated.
     * @param pRealEstateDto The DTO containing the updated real estate information.
     * @param photoFiles      The new photo file of the real estate.
     * @return A ResponseEntity containing the updated real estate DTO if
     *         successful, or an error response if not.
     */
    @PutMapping("/real-estates/{estateId}")
    public ResponseEntity<Object> updateRealEstate(@PathVariable(name = "estateId", required = true) Integer id,
            @Valid @ModelAttribute RealEstateDto pRealEstateDto,
            @RequestParam(name = "photoFiles", required = false) List<MultipartFile> photoFiles) {
        try {
            // Chỉ lưu ảnh và gán ảnh nếu photoFiles không null và không trống
            if (photoFiles != null && !photoFiles.isEmpty()) {
                List<Photo> photos = realEstateService.savePhotos(id, photoFiles);
                pRealEstateDto.setPhotos(photos);
            }
            // convert sang entity
            RealEstate pRealEstate = modelMapper.map(pRealEstateDto, RealEstate.class);
            // xử lý dữ liệu
            RealEstate realEstate = realEstateService.updateRealEstate(id, pRealEstate);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (IOException e) {
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
    @DeleteMapping("/real-estates/{estateId}")
    public ResponseEntity<Object> deleteRealEstate(@PathVariable(name = "estateId") Integer id) {
        try {
            realEstateService.deleteRealEstate(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * complete
     * @param id
     * @return
     */
    @PostMapping("/real-estates/complete/{estateId}")
    public ResponseEntity<Object> completeRealEstate(@PathVariable(name = "estateId") Integer id) {
        try {
            // xử lý dữ liệu
            RealEstate realEstate = realEstateService.completeRealEstate(id);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * restore
     * @param id
     * @return
     */
    @PostMapping("/real-estates/restore/{estateId}")
    public ResponseEntity<Object> restoreRealEstate(@PathVariable(name = "estateId") Integer id) {
        try {
            // xử lý dữ liệu
            RealEstate realEstate = realEstateService.restoreRealEstate(id);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * approve
     * @param id
     * @return
     */
    @PostMapping("/real-estates/approve/{estateId}")
    public ResponseEntity<Object> approveRealEstate(@PathVariable(name = "estateId") Integer id) {
        try {
            // xử lý dữ liệu
            RealEstate realEstate = realEstateService.approveRealEstate(id);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * reject
     * @param id
     * @return
     */
    @PostMapping("/real-estates/reject/{estateId}")
    public ResponseEntity<Object> rejectRealEstate(@PathVariable(name = "estateId") Integer id) {
        try {
            // xử lý dữ liệu
            RealEstate realEstate = realEstateService.rejectRealEstate(id);
            // convert sang dto
            RealEstateDto realEstateDto = modelMapper.map(realEstate, RealEstateDto.class);
            return new ResponseEntity<>(realEstateDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
