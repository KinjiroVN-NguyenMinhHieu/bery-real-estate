package com.devcamp.bery_real_estate.dtos;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import org.springframework.web.multipart.MultipartFile;

import com.devcamp.bery_real_estate.constants.EDirection;
import com.devcamp.bery_real_estate.constants.EFurnitureType;
import com.devcamp.bery_real_estate.constants.ERequest;
import com.devcamp.bery_real_estate.constants.EStatus;
import com.devcamp.bery_real_estate.constants.EType;
import com.devcamp.bery_real_estate.models.Photo;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class RealEstateDto {
    // dùng wrapper(Integer, Long) ở DTO để tránh việc @ModelAttribute tự động gán
    // gtri mặc định khi lẽ ra là null
    private Integer id;
    private String title;
    private EType type;
    private ERequest request;
    private Integer provinceId;
    private String provinceName;
    private Integer districtId;
    private String districtName;
    private Integer wardId;
    private String wardName;
    private Integer streetId;
    private String streetName;
    private Integer projectId;
    private String projectName;

    @NotEmpty(message = "Enter the Real Estate address")
    private String address;

    private Integer employeeId;
    private String employeeUserName;
    private String employeeEmail;
    private String employeeHomePhone;
    private String employeePhoto;
    private Long price;
    private BigDecimal acreage;
    private EDirection direction;
    private String apartCode;
    private byte bedroom;
    private EFurnitureType furnitureType;
    private Integer priceRent;
    private String description;

    // trường lưu file ảnh
    private List<MultipartFile> photoFile;
    // trường lưu ảnh(ko trả về)
    private List<Photo> photos;
    // trường trả về url ảnh
    @SuppressWarnings("unused")
    private List<String> photosUrl;
    private boolean isDeleted;
    private EStatus status;
    private Integer createdById;
    private String createdByUserName;
    private Integer updatedById;
    private String updatedByUserName;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date createdAt;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private Date updatedAt;

    // bỏ qua ko trả về
    @JsonIgnore
    public List<Photo> getPhotos() {
        return photos;
    }

    public List<String> getPhotosUrl() {
        return photos.stream()
                .map(Photo::getUrl)
                .collect(Collectors.toList());
    }
}
