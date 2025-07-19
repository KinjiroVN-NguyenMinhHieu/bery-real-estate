package com.devcamp.bery_real_estate.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.devcamp.bery_real_estate.constants.EDirection;
import com.devcamp.bery_real_estate.constants.EFurnitureType;
import com.devcamp.bery_real_estate.constants.ERequest;
import com.devcamp.bery_real_estate.constants.EType;
import com.devcamp.bery_real_estate.dtos.RealEstateDto;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.devcamp.bery_real_estate.models.Photo;

public interface IRealEstateService {
    /**
     * get list
     * @return
     */
    List<RealEstate> getListRealEstates();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<RealEstate> getAllRealEstates(int page, int size);

    /**
     * get all by status(page)
     * @param page
     * @param size
     * @return
     */
    Page<RealEstate> getAllRealEstatesByStatus(int page, int size);

    /**
     * get all publish
     * @return
     */
    List<RealEstate> getAllPublishedRealEstates();

    /**
     * get all unpublish
     * @return
     */
    List<RealEstate> getAllUnpublishedRealEstates();

    /**
     * count pending
     * @return
     */
    long countPendingRealEstates();

    /**
     * count percent approve
     * @return
     */
    double countPercentageRealEstatesApproved();

    /**
     * get limit publish(page)
     * @param size
     * @return
     */
    Page<RealEstate> getLimitPublishedRealEstates(int size);

    /**
     * get limit unpublish(page)
     * @param size
     * @return
     */
    Page<RealEstate> getLimitUnpublishedRealEstates(int size);

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<RealEstate> getAllRealEstatesWithPagination(int page, int size);

    /**
     * get all luxury(page)
     * @param page
     * @param size
     * @return
     */
    Page<RealEstate> getAllLuxuryRealEstatesWithPagination(int page, int size);

    /**
     * count by province
     * @return
     */
    List<Object[]> countRealEstatesByProvince();

    /**
     * search
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
    Page<RealEstate> searchRealEstatesByKeywordAndFilterWithPagination(String keyword, EType type, ERequest request,
            EFurnitureType furnitureType, EDirection direction, Long minPrice, Long maxPrice, BigDecimal minAcreage,
            BigDecimal maxAcreage, Integer provinceId, int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    RealEstate getRealEstateById(Integer id);

    /**
     * add
     * @param pRealEstate
     * @return
     */
    RealEstate createRealEstate(RealEstate pRealEstate);

    /**
     * update
     * @param id
     * @param pRealEstate
     * @return
     */
    RealEstate updateRealEstate(Integer id, RealEstate pRealEstate);

    /**
     * delete
     * @param id
     */
    void deleteRealEstate(Integer id);
    
    /**
     * complete
     * @param id
     * @return
     */
    RealEstate completeRealEstate(Integer id);
    
    /**
     * restore
     * @param id
     * @return
     */
    RealEstate restoreRealEstate(Integer id);

    /**
     * approve
     * @param id
     * @return
     */
    RealEstate approveRealEstate(Integer id);

    /**
     * reject
     * @param id
     * @return
     */
    RealEstate rejectRealEstate(Integer id);

    /**
     * get current emp
     * @return
     */
    Employee getCurrentEmployee();

    /**
     * save photos
     * @param id
     * @param photoFiles
     * @return
     * @throws IOException
     */
    List<Photo> savePhotos(Integer id, List<MultipartFile> photoFiles) throws IOException;

    /**
     * create new
     * @param pRealEstateDto
     * @return
     */
    RealEstate createAndSaveNewRealEstate(RealEstateDto pRealEstateDto);
}
