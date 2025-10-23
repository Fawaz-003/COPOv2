package com.copo.app.service;

import com.copo.app.model.Department;
import com.copo.app.model.Faculty;
import com.copo.app.repository.DepartmentRepository;
import com.copo.app.repository.FacultyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class FacultyService {

    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PasswordService passwordService;

    /**
     * Save a new faculty after checking for duplicate code.
     */
    public Faculty saveFaculty(Faculty faculty) {
        try {
            if (facultyRepository.existsByFacultycode(faculty.getFacultycode())) {
                logger.warn("Duplicate faculty code detected: {}", faculty.getFacultycode());
                throw new RuntimeException("Faculty already exists with code: " + faculty.getFacultycode());
            }

            // Hash the password if provided
            if (faculty.getPassword() != null && !faculty.getPassword().trim().isEmpty()) {
                String hashedPassword = passwordService.hashPassword(faculty.getPassword());
                faculty.setPassword(hashedPassword);
                logger.info("Password hashed for faculty: {}", faculty.getFacultycode());
            }

            Faculty saved = facultyRepository.save(faculty);
            logger.info("Faculty saved successfully: {}", saved.getFacultycode());
            return saved;

        } catch (Exception e) {
            logger.error("Error saving faculty: {}", faculty.getFacultycode(), e);
            throw new RuntimeException("Failed to save faculty. Error: " + e.getMessage());
        }
    }

    /**
     * Fetch all faculty records.
     */
    public List<Faculty> getAllFaculty() {
        try {
            List<Faculty> list = facultyRepository.findAll();
            logger.info("Fetched all faculty records. Total: {}", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Error fetching all faculty", e);
            throw new RuntimeException("Failed to retrieve faculty list. Error: " + e.getMessage());
        }
    }

    /**
     * Get a faculty by ID.
     */
    public Optional<Faculty> getFacultyById(Long id) {
        try {
            Optional<Faculty> faculty = facultyRepository.findById(id);
            if (faculty.isPresent()) {
                logger.info("Faculty fetched with id: {}", id);
            } else {
                logger.warn("Faculty not found with id: {}", id);
            }
            return faculty;
        } catch (Exception e) {
            logger.error("Error fetching faculty by id: {}", id, e);
            throw new RuntimeException("Failed to get faculty by ID. Error: " + e.getMessage());
        }
    }

    /**
     * Update faculty after checking for duplicate code.
     */
    public Faculty updateFaculty(Long id, Faculty facultyDetails) {
        try {
            Faculty faculty = facultyRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Faculty not found for update with id: {}", id);
                        return new RuntimeException("Faculty not found with id: " + id);
                    });

            if (!faculty.getFacultycode().equals(facultyDetails.getFacultycode())) {
                Optional<Faculty> duplicate = facultyRepository.findByFacultycode(facultyDetails.getFacultycode());
                if (duplicate.isPresent() && !duplicate.get().getId().equals(id)) {
                    logger.warn("Duplicate faculty code '{}' during update", facultyDetails.getFacultycode());
                    throw new RuntimeException("Faculty code already exists: " + facultyDetails.getFacultycode());
                }
            }

            faculty.setFacultycode(facultyDetails.getFacultycode().trim());
            faculty.setName(facultyDetails.getName().trim());
            faculty.setDesignation(facultyDetails.getDesignation().trim());
            faculty.setDepartment(facultyDetails.getDepartment());
            
            // Update password if provided and not empty
            if (facultyDetails.getPassword() != null && !facultyDetails.getPassword().trim().isEmpty()) {
                String hashedPassword = passwordService.hashPassword(facultyDetails.getPassword());
                faculty.setPassword(hashedPassword);
                logger.info("Password updated for faculty: {}", faculty.getFacultycode());
            }

            Faculty updated = facultyRepository.save(faculty);
            logger.info("Faculty updated: {}", updated.getFacultycode());
            return updated;

        } catch (Exception e) {
            logger.error("Error updating faculty with id {}", id, e);
            throw new RuntimeException("Failed to update faculty. Error: " + e.getMessage());
        }
    }

    /**
     * Delete a faculty by ID.
     */
    public void deleteFaculty(Long id) {
        try {
            if (!facultyRepository.existsById(id)) {
                logger.warn("Delete failed: Faculty not found with id {}", id);
                throw new RuntimeException("Faculty not found with id: " + id);
            }

            facultyRepository.deleteById(id);
            logger.info("Faculty deleted with id: {}", id);

        } catch (Exception e) {
            logger.error("Error deleting faculty with id: {}", id, e);
            throw new RuntimeException("Failed to delete faculty. Error: " + e.getMessage());
        }
    }

    /**
     * Upload faculty from CSV with logging, validation, and error tracking.
     */
    @Transactional
    public Map<String, Object> uploadFaculty(MultipartFile file) {
        List<String> uploaded = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int rowNum = 0;

        logger.info("Starting CSV upload for faculty...");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                rowNum++;
                final int currentRow = rowNum;

                try {
                    String[] fields = line.split(",");

                    if (fields.length < 4) {
                        throw new RuntimeException("Expected 4 fields (code,name,designation,departmentName)");
                    }

                    String facultyCode = fields[0].trim();
                    String name = fields[1].trim();
                    String designation = fields[2].trim();
                    String departmentName = fields[3].trim();

                    if (facultyCode.isEmpty() || name.isEmpty() || designation.isEmpty() || departmentName.isEmpty()) {
                        throw new RuntimeException("Missing required field(s)");
                    }

                    if (facultyRepository.existsByFacultycode(facultyCode)) {
                        logger.warn("Skipping duplicate faculty code: {}", facultyCode);
                        errors.add("Row " + currentRow + ": Duplicate faculty code: " + facultyCode);
                        continue;
                    }

                    Department department = departmentRepository.findDepartmentByName(departmentName)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + departmentName));

                    Faculty faculty = Faculty.builder()
                            .facultycode(facultyCode)
                            .name(name)
                            .designation(designation)
                            .department(department)
                            .build();

                    facultyRepository.save(faculty);
                    uploaded.add("Faculty Code : "+facultyCode +" : : Facuty name :"+name);
                    logger.info("Row {}: Faculty uploaded: {}", currentRow, facultyCode);

                } catch (Exception rowEx) {
                    logger.error("Row {} failed to upload: {}", currentRow, rowEx.getMessage());
                    errors.add("Row " + currentRow + ": " + rowEx.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("CSV upload failed due to file error", e);
            throw new RuntimeException("Failed to upload CSV file. Error: " + e.getMessage());
        }

        logger.info("Faculty upload completed. Success: {}, Errors: {}", uploaded.size(), errors.size());

        Map<String, Object> result = new HashMap<>();
        result.put("uploadedFaculties", uploaded);
        result.put("errors", errors);
        return result;
    }
    
    /**
     * Verify faculty credentials (faculty code and password)
     * @param facultyCode the faculty code
     * @param password the plain text password
     * @return the Faculty if credentials are valid, null otherwise
     */
    public Faculty verifyFacultyCredentials(String facultyCode, String password) {
        try {
            Optional<Faculty> facultyOpt = facultyRepository.findByFacultycode(facultyCode);
            
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                if (faculty.getPassword() != null && passwordService.verifyPassword(password, faculty.getPassword())) {
                    logger.info("Faculty credentials verified for: {}", facultyCode);
                    return faculty;
                } else {
                    logger.warn("Invalid password for faculty: {}", facultyCode);
                }
            } else {
                logger.warn("Faculty not found with code: {}", facultyCode);
            }
            
            return null;
        } catch (Exception e) {
            logger.error("Error verifying faculty credentials for: {}", facultyCode, e);
            throw new RuntimeException("Failed to verify faculty credentials. Error: " + e.getMessage());
        }
    }
    
    /**
     * Set a new password for a faculty member
     * @param facultyId the faculty ID
     * @param newPassword the new plain text password
     * @return true if password was updated successfully
     */
    public boolean setFacultyPassword(Long facultyId, String newPassword) {
        try {
            Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
            
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                String hashedPassword = passwordService.hashPassword(newPassword);
                faculty.setPassword(hashedPassword);
                facultyRepository.save(faculty);
                logger.info("Password updated for faculty ID: {}", facultyId);
                return true;
            } else {
                logger.warn("Faculty not found with ID: {}", facultyId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error setting password for faculty ID: {}", facultyId, e);
            throw new RuntimeException("Failed to set faculty password. Error: " + e.getMessage());
        }
    }
    
    /**
     * Set default password "5106" for all faculty members who don't have a password
     * This method should be called once to initialize all faculty with the default password
     */
    public void setDefaultPasswordForAllFaculty() {
        try {
            List<Faculty> allFaculty = facultyRepository.findAll();
            int updatedCount = 0;
            
            for (Faculty faculty : allFaculty) {
                if (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty()) {
                    String hashedPassword = passwordService.hashPassword("5106");
                    faculty.setPassword(hashedPassword);
                    facultyRepository.save(faculty);
                    updatedCount++;
                    logger.info("Set default password for faculty: {}", faculty.getFacultycode());
                }
            }
            
            logger.info("Default password update completed. Updated {} faculty members.", updatedCount);
        } catch (Exception e) {
            logger.error("Error setting default passwords for faculty", e);
            throw new RuntimeException("Failed to set default passwords. Error: " + e.getMessage());
        }
    }
    
    /**
     * Reset password to default "5106" for a specific faculty member
     * @param facultyId the faculty ID
     * @return true if password was reset successfully
     */
    public boolean resetFacultyPasswordToDefault(Long facultyId) {
        try {
            Optional<Faculty> facultyOpt = facultyRepository.findById(facultyId);
            
            if (facultyOpt.isPresent()) {
                Faculty faculty = facultyOpt.get();
                String hashedPassword = passwordService.hashPassword("5106");
                faculty.setPassword(hashedPassword);
                facultyRepository.save(faculty);
                logger.info("Password reset to default for faculty ID: {}", facultyId);
                return true;
            } else {
                logger.warn("Faculty not found with ID: {}", facultyId);
                return false;
            }
        } catch (Exception e) {
            logger.error("Error resetting password for faculty ID: {}", facultyId, e);
            throw new RuntimeException("Failed to reset faculty password. Error: " + e.getMessage());
        }
    }
}
