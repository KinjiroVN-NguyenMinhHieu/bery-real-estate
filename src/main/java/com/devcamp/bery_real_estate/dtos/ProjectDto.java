package com.devcamp.bery_real_estate.dtos;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

import lombok.Data;

@Data
public class ProjectDto {
    private int id;

    @NotEmpty(message = "Name is required")
    private String name;
    private int provinceId;
    private String provinceName;
    private int districtId;
    private String districtName;
    private Integer wardId;
    private String wardName;
    private Integer streetId;
    private String streetName;
    private String address;
    private String slogan;
    private String description;
    private BigDecimal acreage;
    private BigDecimal constructArea;
    private Short numBlock;
    private String numFloors;

    @PositiveOrZero(message = "Number Apartment must be positive or zero")
    private int numApartment;
    private String apartmenttArea;

    @NotNull(message = "Investor is required")
    private int investorId;
    private String investorName;
    private int constructionContractorId;
    private String constructionContractorName;
    private int designUnitId;
    private String designUnitName;
    private String photo;
    private Double latitude;
    private Double longitude;
    private boolean isDeleted;
    private Set<UtilityDto> utilities;
    private Set<RegionLinkDto> regionLinks;
}
