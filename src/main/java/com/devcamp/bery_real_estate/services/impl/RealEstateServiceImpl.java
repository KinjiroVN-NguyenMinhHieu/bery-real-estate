package com.devcamp.bery_real_estate.services.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devcamp.bery_real_estate.configs.StorageConfig;
import com.devcamp.bery_real_estate.constants.EDirection;
import com.devcamp.bery_real_estate.constants.EFurnitureType;
import com.devcamp.bery_real_estate.constants.ERequest;
import com.devcamp.bery_real_estate.constants.EStatus;
import com.devcamp.bery_real_estate.constants.EType;
import com.devcamp.bery_real_estate.dtos.RealEstateDto;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Photo;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;
import com.devcamp.bery_real_estate.repositories.IDistrictRepository;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.repositories.IPhotoRepository;
import com.devcamp.bery_real_estate.repositories.IProjectRepository;
import com.devcamp.bery_real_estate.repositories.IProvinceRepository;
import com.devcamp.bery_real_estate.repositories.IRealEstateRepository;
import com.devcamp.bery_real_estate.repositories.IStreetRepository;
import com.devcamp.bery_real_estate.repositories.IWardRepository;
import com.devcamp.bery_real_estate.repositories.specifications.RealEstateSpecifications;
import com.devcamp.bery_real_estate.services.IRealEstateService;

@Service
public class RealEstateServiceImpl implements IRealEstateService {
        @Autowired
        private IRealEstateRepository realEstateRepository;
        @Autowired
        private IProjectRepository projectRepository;
        @Autowired
        private IProvinceRepository provinceRepository;
        @Autowired
        private IDistrictRepository districtRepository;
        @Autowired
        private IWardRepository wardRepository;
        @Autowired
        private IStreetRepository streetRepository;
        @Autowired
        private IEmployeeRepository employeeRepository;
        @Autowired
        private IPhotoRepository photoRepository;
        @Autowired
        private StorageConfig storageConfig;

        // Lấy url UI
        @Value("${app.base.url}")
        private String appURL;

        @Override
        public List<RealEstate> getListRealEstates() {
                return realEstateRepository.findAll();
        };

        @Override
        public Page<RealEstate> getAllRealEstates(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                return realEstateRepository.findAll(pageable);
        };

        @Override
        public Page<RealEstate> getAllRealEstatesByStatus(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                return realEstateRepository.findAllByStatus(EStatus.PENDING, pageable);
        };

        @Override
        public List<RealEstate> getAllPublishedRealEstates() {
                Employee employee = this.getCurrentEmployee();
                return realEstateRepository.findAllPublishedlByEmployeeId(employee.getId());
        };

        @Override
        public List<RealEstate> getAllUnpublishedRealEstates() {
                Employee employee = this.getCurrentEmployee();
                return realEstateRepository.findAllUnpublishedByEmployeeId(employee.getId());
        };

        @Override
        public long countPendingRealEstates() {
                return realEstateRepository.countByStatus(EStatus.PENDING);
        };

        @Override
        public double countPercentageRealEstatesApproved() {
                long approvedRealEstates = realEstateRepository.countByStatus(EStatus.APPROVED);
                long totalRealEstates = realEstateRepository.count();
                if (totalRealEstates == 0) {
                        return 0;
                }
                // Chuyển đổi ít nhất một trong các số hạng thành double để phép chia không bị làm tròn
                double percentage = ((double) approvedRealEstates / totalRealEstates) * 100;
                // Làm tròn đến 2 chữ số thập phân
                return Math.round(percentage * 100.0) / 100.0;
        };

        @Override
        public Page<RealEstate> getLimitPublishedRealEstates(int size) {
                Sort sort = Sort.by(
                        Sort.Order.desc("status"),
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("updatedAt")
                );
                Pageable pageable = PageRequest.of(0, size, sort);
                Employee employee = this.getCurrentEmployee();
                return realEstateRepository.findLimitPublishedByEmployeeId(employee.getId(), pageable);
        }
        
        @Override    
        public Page<RealEstate> getLimitUnpublishedRealEstates(int size) {
                Sort sort = Sort.by(
                        Sort.Order.desc("status"),
                        Sort.Order.desc("createdAt"),
                        Sort.Order.desc("updatedAt")
                );
                Pageable pageable = PageRequest.of(0, size, sort);
                Employee employee = this.getCurrentEmployee();
                return realEstateRepository.findLimitUnpublishedByEmployeeId(employee.getId(), pageable);
        };

        @Override
        public Page<RealEstate> getAllRealEstatesWithPagination(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                return realEstateRepository.findAllWithPagination(pageable);
        };

        @Override
        public Page<RealEstate> getAllLuxuryRealEstatesWithPagination(int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                return realEstateRepository.findAllLuxuryWithPagination(pageable);
        };

        @Override
        public List<Object[]> countRealEstatesByProvince() {
                return realEstateRepository.countByProvince();
        };

        @Override
        public Page<RealEstate> searchRealEstatesByKeywordAndFilterWithPagination(String keyword, EType type,
                        ERequest request,
                        EFurnitureType furnitureType, EDirection direction, Long minPrice, Long maxPrice,
                        BigDecimal minAcreage,
                        BigDecimal maxAcreage, Integer provinceId, int page, int size) {
                Pageable pageable = PageRequest.of(page, size);
                return realEstateRepository.findAll(
                                RealEstateSpecifications.searchAndFilter(keyword, type, request, furnitureType,
                                                direction, provinceId, minPrice, maxPrice, minAcreage, maxAcreage),
                                pageable);
        };

        @Override
        public RealEstate getRealEstateById(Integer id) {
                return realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real estate not found"));
        };

        @Override
        @Transactional
        public RealEstate createRealEstate(RealEstate pRealEstate) {
                // lấy employee
                Employee currentEmployee = this.getCurrentEmployee();
                // Check các thông tin employee, địa chỉ và xây dựng(ko bắt buộc, ko có thì null)
                Employee employee = (pRealEstate.getEmployee() != null)
                                ? employeeRepository.findById(pRealEstate.getEmployee().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"))
                                                : null;
                Province province = (pRealEstate.getProvince() != null)
                                ? provinceRepository.findById(pRealEstate.getProvince().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Province not found"))
                                : null;
                District district = (pRealEstate.getDistrict() != null)
                                ? districtRepository.findById(pRealEstate.getDistrict().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("District not found"))
                                : null;

                Ward ward = (pRealEstate.getWard() != null) ? wardRepository.findById(pRealEstate.getWard().getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) : null;

                Street street = (pRealEstate.getStreet() != null)
                                ? streetRepository.findById(pRealEstate.getStreet().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Street not found"))
                                : null;

                Project project = (pRealEstate.getProject() != null)
                                ? projectRepository.findById(pRealEstate.getProject().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Project not found"))
                                : null;

                // Thêm các thuộc tính chưa thêm và gán lại các thuộc tính có thể null
                if (employee != null) {
                        pRealEstate.setEmployee(employee);
                } else {
                        pRealEstate.setEmployee(currentEmployee);
                }
                pRealEstate.setProvince(province);
                pRealEstate.setDistrict(district);
                pRealEstate.setWard(ward);
                pRealEstate.setStreet(street);
                pRealEstate.setProject(project);
                pRealEstate.setCreatedBy(currentEmployee);
                pRealEstate.setUpdatedBy(null);
                pRealEstate.setUpdatedAt(null);
                return realEstateRepository.save(pRealEstate);
        };

        @Override
        @Transactional
        public RealEstate updateRealEstate(Integer id, RealEstate pRealEstate) {
                // lấy employee
                Employee currentEmployee = this.getCurrentEmployee();
                // check bđs
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));
                // Check các thông tin employee, địa chỉ và xây dựng
                Employee employee = (pRealEstate.getEmployee() != null)
                                ? employeeRepository.findById(pRealEstate.getEmployee().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"))
                                                : null;
                Province province = (pRealEstate.getProvince() != null)
                                ? provinceRepository.findById(pRealEstate.getProvince().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Province not found"))
                                : null;
                District district = (pRealEstate.getDistrict() != null)
                                ? districtRepository.findById(pRealEstate.getDistrict().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("District not found"))
                                : null;

                Ward ward = (pRealEstate.getWard() != null) ? wardRepository.findById(pRealEstate.getWard().getId())
                                .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) : null;

                Street street = (pRealEstate.getStreet() != null)
                                ? streetRepository.findById(pRealEstate.getStreet().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Street not found"))
                                : null;

                Project project = (pRealEstate.getProject() != null)
                                ? projectRepository.findById(pRealEstate.getProject().getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Project not found"))
                                : null;
                // update
                if (employee != null) {
                        pRealEstate.setEmployee(employee);
                } else {
                        pRealEstate.setEmployee(currentEmployee);
                }
                realEstate.setProvince(province);
                realEstate.setDistrict(district);
                realEstate.setWard(ward);
                realEstate.setStreet(street);
                realEstate.setProject(project);
                realEstate.setTitle(pRealEstate.getTitle());
                realEstate.setType(pRealEstate.getType());
                realEstate.setRequest(pRealEstate.getRequest());
                realEstate.setAddress(pRealEstate.getAddress());
                realEstate.setPrice(pRealEstate.getPrice());
                realEstate.setAcreage(pRealEstate.getAcreage());
                realEstate.setDirection(pRealEstate.getDirection());
                realEstate.setApartCode(pRealEstate.getApartCode());
                realEstate.setBedroom(pRealEstate.getBedroom());
                realEstate.setFurnitureType(pRealEstate.getFurnitureType());
                realEstate.setPriceRent(pRealEstate.getPriceRent());
                realEstate.setDescription(pRealEstate.getDescription());
                
                if (pRealEstate.getPhotos() != null && !pRealEstate.getPhotos().isEmpty()) {
                        realEstate.setPhotos(pRealEstate.getPhotos());
                }   
                realEstate.setDeleted(pRealEstate.isDeleted());

                if (pRealEstate.isDeleted()) {
                        realEstate.setStatus(EStatus.REMOVED);
                } else if (pRealEstate.getStatus() != null && !pRealEstate.isDeleted()) {
                        realEstate.setStatus(pRealEstate.getStatus());
                }

                realEstate.setUpdatedAt(new Date());
                realEstate.setUpdatedBy(currentEmployee);
                return realEstateRepository.save(realEstate);
        };

        @Override
        public void deleteRealEstate(Integer id) {
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));
                realEstate.setDeleted(true);
                realEstate.setStatus(EStatus.REMOVED);
                realEstate.setUpdatedAt(new Date());
                Employee currentEmployee = this.getCurrentEmployee();
                realEstate.setUpdatedBy(currentEmployee);
                realEstateRepository.save(realEstate);
        };

        @Override
        public RealEstate completeRealEstate(Integer id) {
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));
                realEstate.setStatus(EStatus.COMPLETED);
                realEstate.setUpdatedAt(new Date());
                Employee currentEmployee = this.getCurrentEmployee();
                realEstate.setUpdatedBy(currentEmployee);
                return realEstateRepository.save(realEstate);
        };

        @Override
        public RealEstate restoreRealEstate(Integer id) {
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));
                realEstate.setDeleted(false);
                realEstate.setStatus(EStatus.PENDING);
                realEstate.setUpdatedAt(new Date());
                Employee currentEmployee = this.getCurrentEmployee();
                realEstate.setUpdatedBy(currentEmployee);
                return realEstateRepository.save(realEstate);
        };

        @Override
        public RealEstate approveRealEstate(Integer id) {
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));
                realEstate.setStatus(EStatus.APPROVED);
                realEstate.setUpdatedAt(new Date());
                Employee currentEmployee = this.getCurrentEmployee();
                realEstate.setUpdatedBy(currentEmployee);
                return realEstateRepository.save(realEstate);
        };

        @Override
        public RealEstate rejectRealEstate(Integer id) {
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));
                realEstate.setStatus(EStatus.REJECTED);
                realEstate.setUpdatedAt(new Date());
                Employee currentEmployee = this.getCurrentEmployee();
                realEstate.setUpdatedBy(currentEmployee);
                return realEstateRepository.save(realEstate);
        };

        /**
         * Phương thức để lấy thông tin của nhân viên hiện tại dựa trên thông tin xác
         * thực của người dùng.
         * 
         * @return Đối tượng Employee đang được xác thực.
         * @throws UsernameNotFoundException Nếu không tìm thấy nhân viên với username
         *                                   được xác thực.
         */
        @Override
        public Employee getCurrentEmployee() {
                // Lấy đối tượng Authentication từ SecurityContextHolder
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                // Lấy username từ đối tượng Authentication
                String username = authentication.getName();

                // Tìm kiếm nhân viên trong cơ sở dữ liệu bằng username
                return employeeRepository.findByUserName(username)
                                // Ném ngoại lệ nếu không tìm thấy nhân viên với username được xác thực
                                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        }

        // Hàm lưu ảnh và trả về danh sách ảnh
        @Override
        public List<Photo> savePhotos(Integer id, List<MultipartFile> photoFiles) throws IOException {
                if (photoFiles.isEmpty()) {
                        throw new IOException("Photos are required");
                }

                // Xóa toàn bộ ảnh cũ trước khi lưu ảnh mới
                deleteOldPhotos(id);

                List<Photo> savedPhotos = new ArrayList<>();

                for (MultipartFile photoFile : photoFiles) {
                        Photo savedPhoto = savePhoto(id, photoFile);
                        savedPhotos.add(savedPhoto);
                }

                return savedPhotos;
        }

        //Hàm tạo 1 ảnh
        private Photo savePhoto(Integer id, MultipartFile photoFile) throws IOException {
                // check bđs
                RealEstate realEstate = realEstateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));

                // Xây dựng đường dẫn lưu trữ dựa trên đường dẫn cấu hình và tên tệp tin
                StringBuilder builder = new StringBuilder();
                builder.append(storageConfig.getLocation() + "properties/");
                builder.append(String.valueOf(id)).append("/");
                builder.append(photoFile.getOriginalFilename());
                Path path = Paths.get(builder.toString());

                // Lấy dữ liệu MultipartFile
                byte[] bytes = photoFile.getBytes();

                // Tạo thư mục cha nếu nó không tồn tại
                Files.createDirectories(path.getParent());

                // Ghi dữ liệu từ MultipartFile vào tệp tin
                Files.write(path, bytes);

                // Tạo đường dẫn tương đối để truy cập từ client và thêm vào danh sách link ảnh
                String photoUrl = appURL + "/images/properties/" + id + "/" + photoFile.getOriginalFilename();

                // Xử lý và lưu trữ ảnh vào cơ sở dữ liệu
                Photo photo = new Photo();
                photo.setRealEstate(realEstate);
                photo.setName(photoFile.getOriginalFilename());
                photo.setUrl(photoUrl);
                return photoRepository.save(photo);
        }

        //Hàm xóa bỏ file trong folder cũ đi để update ảnh
        private void deleteOldPhotos(Integer id) throws IOException {
                // Xóa các bản ghi photo trong cơ sở dữ liệu trước
                List<Photo> photosToDelete = photoRepository.findByRealEstateId(id);
                if (!photosToDelete.isEmpty()) {
                        photoRepository.deleteAll(photosToDelete);
                }

                // Đường dẫn thư mục chứa các ảnh
                Path directoryPath = Paths.get(storageConfig.getLocation() + "properties/").resolve(String.valueOf(id));

                // Kiểm tra xem thư mục tồn tại và có phải là một thư mục không
                if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
                        // Xóa toàn bộ các tệp tin trong thư mục
                        Files.walk(directoryPath)//duyệt qua tất cả các tệp và thư mục trong một cây thư mục
                        .filter(Files::isRegularFile)//lọc ra chỉ các tệp thực sự, loại bỏ các thư mục và các loại tệp khác.
                        .forEach(file -> {
                                try {
                                Files.delete(file);
                                } catch (IOException e) {
                                e.printStackTrace();
                                }
                        });
                }
        }

        //Hàm tạo mới 1 realestate để lấy id
        @Override
        public RealEstate createAndSaveNewRealEstate(RealEstateDto pRealEstateDto) {
                // Tạo mới 1 realestate để lấy id
                RealEstate newRealEstate = new RealEstate();
                // Thêm các thuộc tính bắt buộc
                newRealEstate.setAddress(pRealEstateDto.getAddress());
                newRealEstate.setBedroom(pRealEstateDto.getBedroom());
                newRealEstate.setPrice(pRealEstateDto.getPrice());
                newRealEstate.setRequest(pRealEstateDto.getRequest());
                newRealEstate.setType(pRealEstateDto.getType());
                // Lưu đối tượng mới vào cơ sở dữ liệu, ID sẽ được tự động tăng
                return realEstateRepository.save(newRealEstate);
        }
}
