package com.devcamp.bery_real_estate.repositories.specifications;

import java.math.BigDecimal;

import org.springframework.data.jpa.domain.Specification;

import com.devcamp.bery_real_estate.constants.EDirection;
import com.devcamp.bery_real_estate.constants.EFurnitureType;
import com.devcamp.bery_real_estate.constants.ERequest;
import com.devcamp.bery_real_estate.constants.EStatus;
import com.devcamp.bery_real_estate.constants.EType;
import com.devcamp.bery_real_estate.entities.RealEstate;

public class RealEstateSpecifications {
    /**
     * check title
     * @param title
     * @return
     */
    public static Specification<RealEstate> hasTitle(String title) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("title"),
                "%" + title.toUpperCase() + "%");
    }

    /**
     * check description
     * @param description
     * @return
     */
    public static Specification<RealEstate> hasDescription(String description) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("description"),
                "%" + description.toUpperCase() + "%");
    }

    /**
     * check address
     * @param address
     * @return
     */
    public static Specification<RealEstate> hasAddress(String address) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("address"),
                "%" + address.toUpperCase() + "%");
    }

    /**
     * check apart code
     * @param apartCode
     * @return
     */
    public static Specification<RealEstate> hasApartCode(String apartCode) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("apartCode"),
                "%" + apartCode.toUpperCase() + "%");
    }

    /**
     * check type
     * @param type
     * @return
     */
    public static Specification<RealEstate> hasType(EType type) {
        return (root, query, criteriaBuilder) -> {
            if (type != null) {
                return criteriaBuilder.equal(root.get("type"), type);
            }
            return null;
        };
    }

    /**
     * check request
     * @param request
     * @return
     */
    public static Specification<RealEstate> hasRequest(ERequest request) {
        return (root, query, criteriaBuilder) -> {
            if (request != null) {
                return criteriaBuilder.equal(root.get("request"), request);
            }
            return null;
        };
    }

    /**
     * check furniture type
     * @param furnitureType
     * @return
     */
    public static Specification<RealEstate> hasFurnitureType(EFurnitureType furnitureType) {
        return (root, query, criteriaBuilder) -> {
            if (furnitureType != null) {
                return criteriaBuilder.equal(root.get("furnitureType"), furnitureType);
            }
            return null;
        };
    }

    /**
     * check direction
     * @param direction
     * @return
     */
    public static Specification<RealEstate> hasDirection(EDirection direction) {
        return (root, query, criteriaBuilder) -> {
            if (direction != null) {
                return criteriaBuilder.equal(root.get("direction"), direction);
            }
            return null;
        };
    }

    /**
     * check province id
     * @param provinceId
     * @return
     */
    public static Specification<RealEstate> hasProvinceId(Integer provinceId) {
        return (root, query, criteriaBuilder) -> {
            if (provinceId != null) {
                return criteriaBuilder.equal(root.get("province").get("id"), provinceId);
            }
            return null;
        };
    }

    /**
     * check price range
     * @param minPrice
     * @param maxPrice
     * @return
     */
    public static Specification<RealEstate> priceRange(Long minPrice, Long maxPrice) {
        return (root, query, criteriaBuilder) -> {
            if (minPrice != null && maxPrice != null) {
                return criteriaBuilder.between(root.get("price"), minPrice, maxPrice);
            } else if (minPrice != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice);
            } else if (maxPrice != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice);
            }
            return null;
        };
    }

    /**
     * check acreage range
     * @param minAcreage
     * @param maxAcreage
     * @return
     */
    public static Specification<RealEstate> acreageRange(BigDecimal minAcreage, BigDecimal maxAcreage) {
        return (root, query, criteriaBuilder) -> {
            if (minAcreage != null && maxAcreage != null) {
                return criteriaBuilder.between(root.get("acreage"), minAcreage, maxAcreage);
            } else if (minAcreage != null) {
                return criteriaBuilder.greaterThanOrEqualTo(root.get("acreage"), minAcreage);
            } else if (maxAcreage != null) {
                return criteriaBuilder.lessThanOrEqualTo(root.get("acreage"), maxAcreage);
            }
            return null;
        };
    }

    /**
     * check delete
     * @return
     */
    public static Specification<RealEstate> isNotDeleted() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("isDeleted"), false);
        };
    }

    /**
     * check status approved
     * @return
     */
    public static Specification<RealEstate> statusIsApproved() {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.get("status"), EStatus.APPROVED);
        };
    }

    // This method assumes that you are searching for a keyword that could be in any
    // of the specified fields.
    /**
     * 
     * @param keyword
     * @return
     */
    public static Specification<RealEstate> keywordSearch(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null) {
                return criteriaBuilder.conjunction(); // Trả về một Predicate rỗng(tức k có điều kiện search và ko ảnh
                                                      // hưởng tới các tiêu chí khác)
            }
            String keywordUpper = keyword.toUpperCase();// toUpperCase() ko hoạt động với null

            // dùng as(String.class để chuyển enum sang string để so sánh)
            // dùng criteriaBuilder.upper để chuyển nó sang chữ hoa
            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("title")), "%" + keywordUpper + "%"),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("description")), "%" + keywordUpper + "%"),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("address")), "%" + keywordUpper + "%"),
                    criteriaBuilder.like(criteriaBuilder.upper(root.get("apartCode")), "%" + keywordUpper + "%"),
                    criteriaBuilder.equal(root.get("type").as(String.class), keywordUpper),
                    criteriaBuilder.equal(root.get("request").as(String.class), keywordUpper),
                    criteriaBuilder.equal(root.get("furnitureType").as(String.class), keywordUpper),
                    criteriaBuilder.equal(root.get("direction").as(String.class), keywordUpper));
        };
    }

    /**
     * Phương thức này tìm kiếm và lọc sử dụng kết hợp tất cả các trường nếu ko null
     * @param keyword
     * @param type
     * @param request
     * @param furnitureType
     * @param direction
     * @param provinceId
     * @param minPrice
     * @param maxPrice
     * @param minAcreage
     * @param maxAcreage
     * @return
     */
    public static Specification<RealEstate> searchAndFilter(String keyword, EType type, ERequest request,
            EFurnitureType furnitureType, EDirection direction,
            Integer provinceId, Long minPrice, Long maxPrice,
            BigDecimal minAcreage, BigDecimal maxAcreage) {
        // Kiểm tra null trc khi tìm kiếm, nếu null thì trả về 1 Predicate rỗng(tức k có
        // điều kiện search và ko ảnh
        // hưởng tới các tiêu chí khác)
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.and(
                    keyword != null ? keywordSearch(keyword).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    type != null ? hasType(type).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    request != null ? hasRequest(request).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    furnitureType != null ? hasFurnitureType(furnitureType).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    direction != null ? hasDirection(direction).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    provinceId != null ? hasProvinceId(provinceId).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    minPrice != null || maxPrice != null
                            ? priceRange(minPrice, maxPrice).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    minAcreage != null || maxAcreage != null
                            ? acreageRange(minAcreage, maxAcreage).toPredicate(root, query, criteriaBuilder)
                            : criteriaBuilder.conjunction(),
                    // Thêm điều kiện is_deleted = false vào đây
                    isNotDeleted().toPredicate(root, query, criteriaBuilder),
                    // Thêm điều kiện status = approved vào đây
                    statusIsApproved().toPredicate(root, query, criteriaBuilder)
            );
        };
    }

}
