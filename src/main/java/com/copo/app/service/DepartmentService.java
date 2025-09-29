package com.copo.app.service;

import com.copo.app.model.Department;
import com.copo.app.model.Section;
import com.copo.app.repository.DepartmentRepository;
import com.copo.app.repository.SectionRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentService.class);

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private SectionRepository sectionRepository;

    // Save Department with Validation
    @Transactional
    public Department saveDepartment(Department department) {
        try {
        	
        	String name = department.getName().trim();
            Optional<Department> existing = departmentRepository.findDepartmentByName(name);
            if (existing.isPresent()) {
                logger.warn("Attempt to save duplicate department: {}", department.getName());
                throw new RuntimeException("Department already exists with name: " + department.getName());
            }

            Department saved = departmentRepository.save(department);
            logger.info("Department saved successfully: {}", saved.getName());
            return saved;

        } catch (Exception e) {
            logger.error("Error while saving department: {}", department.getName(), e);
            throw new RuntimeException("Failed to save department: " + department.getName() +" , Error :"+e.getMessage());
        }
    }

    public void createSectionsForDepartment(Department department, List<String> sectionNames) {
        if (sectionNames == null || sectionNames.isEmpty()) {
            return;
        }
        for (String secName : sectionNames) {
            if (secName == null || secName.trim().isEmpty()) continue;
            Section section = Section.builder()
                    .sectionName(secName.trim())
                    .department(department)
                    .build();
            sectionRepository.save(section);
        }
    }

    // Get All Departments (only active/non-deleted)
    public List<Department> getAllDepartments() {
        try {
            List<Department> list = departmentRepository.findAllActive();
            logger.info("Fetched {} active departments", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Error while fetching departments", e);
            throw new RuntimeException("Failed to retrieve department list."+" , Error :"+e.getMessage());
        }
    }

    // Get Department by ID
    public Optional<Department> getDepartmentById(Long id) {
        try {
            Optional<Department> department = departmentRepository.findById(id);
            if (department.isPresent()) {
                logger.info("Department found with id {}", id);
            } else {
                logger.warn("Department not found with id {}", id);
            }
            return department;
        } catch (Exception e) {
            logger.error("Error while fetching department with id {}", id, e);
            throw new RuntimeException("Failed to get department with id: " + id+" , Error :"+e.getMessage());
        }
    }

    // Update Department with Validation
    @Transactional
    public Department updateDepartment(Long id, Department departmentDetails) {
        try {
            Optional<Department> optionalDepartment = departmentRepository.findById(id);
            if (optionalDepartment.isEmpty()) {
                logger.warn("Department not found with id {}", id);
                throw new RuntimeException("Department not found with id: " + id);
            }

            // Check if another department already has the same name
            Optional<Department> duplicate = departmentRepository.findDepartmentByName(departmentDetails.getName());
            if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                logger.warn("Attempt to update department to duplicate name: {}", departmentDetails.getName());
                throw new RuntimeException("Another department already exists with name: " + departmentDetails.getName());
            }

            Department department = optionalDepartment.get();
            department.setName(departmentDetails.getName());
            department.setDescription(departmentDetails.getDescription());
            Department updated = departmentRepository.save(department);
            logger.info("Department updated successfully with id {}", id);
            return updated;

        } catch (Exception e) {
            logger.error("Error while updating department with id {}", id, e);
            throw new RuntimeException("Failed to update department with id: " + id+" , Error :"+e.getMessage());
        }
    }

    public void syncSectionsForDepartment(Department department, List<String> desiredSectionNames) {
        // Normalize input
        List<String> desired = desiredSectionNames == null ? List.of() : desiredSectionNames.stream()
                .filter(s -> s != null && !s.trim().isEmpty())
                .map(String::trim)
                .distinct()
                .toList();

        // Load current sections for the department
        List<Section> current = sectionRepository.findByDepartment_Id(department.getId());

        // Delete sections that are no longer desired
        for (Section sec : current) {
            if (!desired.contains(sec.getSectionName())) {
                sectionRepository.delete(sec);
            }
        }

        // Add missing desired sections
        for (String name : desired) {
            boolean exists = current.stream().anyMatch(s -> s.getSectionName().equalsIgnoreCase(name));
            if (!exists) {
                sectionRepository.save(Section.builder()
                        .sectionName(name)
                        .department(department)
                        .build());
            }
        }
    }

    // Soft Delete Department (mark as deleted instead of actually deleting)
    @Transactional
    public void deleteDepartment(Long id) {
        try {
        	// Check if department exists and is not already deleted
        	Optional<Department> departmentOpt = departmentRepository.findById(id);
        	if (departmentOpt.isEmpty()) {
        	    logger.warn("Attempt to delete non-existing department with id {}", id);
        	    throw new RuntimeException("Department not found with id: " + id);
        	}
        	
        	Department department = departmentOpt.get();
        	if (department.getIsDeleted()) {
        	    logger.warn("Attempt to delete already deleted department with id {}", id);
        	    throw new RuntimeException("Department is already deleted");
        	}
        	
        	// Soft delete - mark as deleted instead of actually deleting
        	departmentRepository.softDeleteById(id);
            logger.info("Department soft deleted with id {}", id);
            
        } catch (Exception e) {
            logger.error("Error while soft deleting department with id {}", id, e);
            throw new RuntimeException("Failed to delete department: " + e.getMessage());
        }
    }

    // Get Department by Name (only active)
    public Department getDepartmentByName(String departmentName) {
        try {
            Department found = departmentRepository.findActiveDepartmentByName(departmentName)
                    .orElseThrow(() -> {
                        logger.warn("Active department not found: {}", departmentName);
                        return new RuntimeException("Department not found: " + departmentName);
                    });
            logger.info("Active department found by name: {}", departmentName);
            return found;
        } catch (Exception e) {
            logger.error("Error while fetching department by name: {}", departmentName, e);
            throw new RuntimeException("Failed to get department by name: " + departmentName+" , Error :"+e.getMessage());
        }
    }
    
}
