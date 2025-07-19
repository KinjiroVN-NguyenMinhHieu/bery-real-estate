package com.devcamp.bery_real_estate.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devcamp.bery_real_estate.entities.ConstructionContractor;
import com.devcamp.bery_real_estate.entities.DesignUnit;
import com.devcamp.bery_real_estate.entities.Investor;
import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.entities.RegionLink;
import com.devcamp.bery_real_estate.entities.Utility;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;
import com.devcamp.bery_real_estate.repositories.IConstructionContractorRepository;
import com.devcamp.bery_real_estate.repositories.IDesignUnitRepository;
import com.devcamp.bery_real_estate.repositories.IDistrictRepository;
import com.devcamp.bery_real_estate.repositories.IInvestorRepository;
import com.devcamp.bery_real_estate.repositories.IProjectRepository;
import com.devcamp.bery_real_estate.repositories.IProvinceRepository;
import com.devcamp.bery_real_estate.repositories.IRegionLinkRepository;
import com.devcamp.bery_real_estate.repositories.IStreetRepository;
import com.devcamp.bery_real_estate.repositories.IUtilityRepository;
import com.devcamp.bery_real_estate.repositories.IWardRepository;
import com.devcamp.bery_real_estate.services.IProjectService;

@Service
public class ProjectServiceImpl implements IProjectService {
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
    private IInvestorRepository investorRepository;
    @Autowired
    private IConstructionContractorRepository constructionContractorRepository;
    @Autowired
    private IDesignUnitRepository designUnitRepository;
    @Autowired
    private IUtilityRepository utilityRepository;
    @Autowired
    private IRegionLinkRepository regionLinkRepository;

    @Override
    public List<Project> getListProjects() {
        return projectRepository.findAll();
    };

    @Override
    public Page<Project> getAllProjects(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return projectRepository.findAll(pageable);
    };

    @Override
    public Project getProjectById(Integer id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("The project not found"));
    };

    @Override
    public Set<Project> getProjectsByDistrictId(Integer id) {
        return projectRepository.findByDistrictId(id);
    };

    @Override
    @Transactional
    public Project createProject(Project pProject) {
        // Check và xây dựng thông tin địa chỉ
        Province province = provinceRepository.findById(pProject.getProvince().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Province not found"));
    
        District district = districtRepository.findById(pProject.getDistrict().getId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found"));
        
        Ward ward = (pProject.getWard().getId() > 0) 
        ? wardRepository.findById(pProject.getWard().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) 
        : null;
        
        Street street = (pProject.getStreet().getId() > 0) 
        ? streetRepository.findById(pProject.getStreet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Street not found")) 
        : null;
        
        Investor investor = investorRepository.findById(pProject.getInvestor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));
        
        ConstructionContractor constructionContractor = constructionContractorRepository.findById(pProject.getConstructionContractor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Construction Contractor not found"));
        
        DesignUnit designUnit = designUnitRepository.findById(pProject.getDesignUnit().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Design Unit not found"));

        // Kiểm tra ManyToMany
        Set<Utility> utilities = new HashSet<>();
        if (pProject.getUtilities() != null) {
        for (Utility utility : pProject.getUtilities()) {
                utility = utilityRepository.findById(utility.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utility not found"));
                utilities.add(utility);
        }
        }

        Set<RegionLink> regionLinks = new HashSet<>();
        if (pProject.getRegionLinks() != null) {
        for (RegionLink regionLink : pProject.getRegionLinks()) {
                regionLink = regionLinkRepository.findById(regionLink.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Region Link not found"));
                regionLinks.add(regionLink);
        }
        }
        // Tạo mới project và thêm thuộc tính
        Project project = new Project();
        project.setProvince(province);
        project.setDistrict(district);
        project.setWard(ward);
        project.setStreet(street);
        project.setInvestor(investor);
        project.setConstructionContractor(constructionContractor);
        project.setDesignUnit(designUnit);
        project.setUtilities(utilities);
        project.setRegionLinks(regionLinks);
        project.setName(pProject.getName());
        project.setAddress(pProject.getAddress());
        project.setSlogan(pProject.getSlogan());
        project.setDescription(pProject.getDescription());
        project.setAcreage(pProject.getAcreage());
        project.setConstructArea(pProject.getConstructArea());
        project.setNumBlock(pProject.getNumBlock());
        project.setNumFloors(pProject.getNumFloors());
        project.setNumApartment(pProject.getNumApartment());
        project.setApartmenttArea(pProject.getApartmenttArea());
        project.setPhoto(pProject.getPhoto());
        project.setLatitude(pProject.getLatitude());
        project.setLongitude(pProject.getLongitude());
        project.setDeleted(pProject.isDeleted());
        return projectRepository.save(project);
    };

    @Override
    @Transactional
    public Project updateProject(Integer id, Project pProject) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        // Check và xây dựng thông tin địa chỉ
        Province province = provinceRepository.findById(pProject.getProvince().getId())
        .orElseThrow(() -> new ResourceNotFoundException("Province not found"));

        District district = districtRepository.findById(pProject.getDistrict().getId())
                .orElseThrow(() -> new ResourceNotFoundException("District not found"));

        Ward ward = (pProject.getWard().getId() > 0) 
        ? wardRepository.findById(pProject.getWard().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ward not found")) 
        : null;

        Street street = (pProject.getStreet().getId() > 0) 
        ? streetRepository.findById(pProject.getStreet().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Street not found")) 
        : null;

        Investor investor = investorRepository.findById(pProject.getInvestor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Investor not found"));

        ConstructionContractor constructionContractor = constructionContractorRepository.findById(pProject.getConstructionContractor().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Construction Contractor not found"));

        DesignUnit designUnit = designUnitRepository.findById(pProject.getDesignUnit().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Design Unit not found"));

        // Kiểm tra ManyToMany
        Set<Utility> utilities = new HashSet<>();
        if (pProject.getUtilities() != null) {
        for (Utility utility : pProject.getUtilities()) {
                utility = utilityRepository.findById(utility.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Utility not found"));
                utilities.add(utility);
        }
        }

        Set<RegionLink> regionLinks = new HashSet<>();
        if (pProject.getRegionLinks() != null) {
        for (RegionLink regionLink : pProject.getRegionLinks()) {
                regionLink = regionLinkRepository.findById(regionLink.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Region Link not found"));
                regionLinks.add(regionLink);
        }
        }
        // Thêm thuộc tính
        project.setProvince(province);
        project.setDistrict(district);
        project.setWard(ward);
        project.setStreet(street);
        project.setInvestor(investor);
        project.setConstructionContractor(constructionContractor);
        project.setDesignUnit(designUnit);
        project.setUtilities(utilities);
        project.setRegionLinks(regionLinks);
        project.setName(pProject.getName());
        project.setAddress(pProject.getAddress());
        project.setSlogan(pProject.getSlogan());
        project.setDescription(pProject.getDescription());
        project.setAcreage(pProject.getAcreage());
        project.setConstructArea(pProject.getConstructArea());
        project.setNumBlock(pProject.getNumBlock());
        project.setNumFloors(pProject.getNumFloors());
        project.setNumApartment(pProject.getNumApartment());
        project.setApartmenttArea(pProject.getApartmenttArea());
        project.setPhoto(pProject.getPhoto());
        project.setLatitude(pProject.getLatitude());
        project.setLongitude(pProject.getLongitude());
        project.setDeleted(pProject.isDeleted());
        return projectRepository.save(project);
    };

    @Override
    public void deleteProject(Integer id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found"));
        project.setDeleted(true);
        projectRepository.save(project);
    };
}
