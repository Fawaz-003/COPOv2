package com.copo.app.service;

import com.copo.app.model.Department;
import com.copo.app.model.Subject;
import com.copo.app.repository.DepartmentRepository;
import com.copo.app.repository.SubjectRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class SubjectService {

    private static final Logger logger = LoggerFactory.getLogger(SubjectService.class);

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Get filtered subjects by dept and/or semester
    public List<Subject> getFilteredSubjects(Long departmentId, Integer semester) {
        try {
            if (departmentId != null && semester != null) {
                logger.info("Fetching subjects by departmentId {} and semester {}", departmentId, semester);
                return subjectRepository.findByDepartmentIdAndSemester(departmentId, semester);
            } else if (departmentId != null) {
                logger.info("Fetching subjects by departmentId {}", departmentId);
                return subjectRepository.findByDepartmentId(departmentId);
            } else if (semester != null) {
                logger.info("Fetching subjects by semester {}", semester);
                return subjectRepository.findBySemester(semester);
            } else {
                logger.info("Fetching all subjects");
                return subjectRepository.findAll();
            }
        } catch (Exception e) {
            logger.error("Error while fetching filtered subjects", e);
            throw new RuntimeException("Failed to fetch subjects. Error: " + e.getMessage());
        }
    }

    public List<Subject> getSubjectsByDepartmentAndSemester(Long departmentId, Integer semester) {
        try {
            return subjectRepository.findByDepartmentIdAndSemester(departmentId, semester);
        } catch (Exception e) {
            logger.error("Error fetching subjects by department and semester", e);
            throw new RuntimeException("Failed to fetch subjects. Error: " + e.getMessage());
        }
    }

    // Save Subject with validation
    @Transactional
    public Subject saveSubject(Subject subject) {
        try {
            String name = subject.getName().trim();
            String code = subject.getCode().trim();
            Department department = subject.getDepartment();
            int semester = subject.getSemester();

            Optional<Subject> duplicateName = subjectRepository.findByNameAndDepartmentAndSemester(name, department, semester);
            if (duplicateName.isPresent()) {
                logger.warn("Duplicate subject name '{}' found in same department and semester", name);
                throw new RuntimeException("Subject name already exists in this department and semester: " + name);
            }

            Optional<Subject> duplicateCode = subjectRepository.findByCodeAndDepartmentAndSemester(code, department, semester);
            if (duplicateCode.isPresent()) {
                logger.warn("Duplicate subject code '{}' found in same department and semester", code);
                throw new RuntimeException("Subject code already exists in this department and semester: " + code);
            }

            subject.setName(name); // Trimmed name
            subject.setCode(code); // Trimmed code

            Subject saved = subjectRepository.save(subject);
            logger.info("Subject saved: {}", saved.getName());
            return saved;
        } catch (Exception e) {
            logger.error("Error saving subject: {}", subject.getName(), e);
            throw new RuntimeException("Failed to save subject. Error: " + e.getMessage());
        }
    }


    public List<Subject> getAllSubjects() {
        try {
            List<Subject> list = subjectRepository.findAll();
            logger.info("Fetched {} subjects", list.size());
            return list;
        } catch (Exception e) {
            logger.error("Error fetching all subjects", e);
            throw new RuntimeException("Failed to fetch subject list. Error: " + e.getMessage());
        }
    }

    public Optional<Subject> getSubjectById(Long id) {
        try {
            return subjectRepository.findById(id);
        } catch (Exception e) {
            logger.error("Error fetching subject by id {}", id, e);
            throw new RuntimeException("Failed to fetch subject with id: " + id + ". Error: " + e.getMessage());
        }
    }

    @Transactional
    public Subject updateSubject(Long id, Subject subjectDetails) {
        try {
            Subject subject = subjectRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Subject not found with id {}", id);
                        return new RuntimeException("Subject not found with id: " + id);
                    });

            String newName = subjectDetails.getName().trim();
            String newCode = subjectDetails.getCode().trim();
            Department newDepartment = subjectDetails.getDepartment();
            int newSemester = subjectDetails.getSemester();

            // Check duplicate name in same department and semester (excluding current subject)
            Optional<Subject> duplicateName = subjectRepository.findByNameAndDepartmentAndSemester(newName, newDepartment, newSemester);
            if (duplicateName.isPresent() && !duplicateName.get().getId().equals(id)) {
                throw new RuntimeException("Duplicate subject name in same department and semester: " + newName);
            }

            Optional<Subject> duplicateCode = subjectRepository.findByCodeAndDepartmentAndSemester(newCode, newDepartment, newSemester);
            if (duplicateCode.isPresent() && !duplicateCode.get().getId().equals(id)) {
                throw new RuntimeException("Duplicate subject code in same department and semester: " + newCode);
            }

            subject.setName(newName);
            subject.setCode(newCode);
            subject.setDepartment(newDepartment);
            subject.setSemester(newSemester);

            Subject updated = subjectRepository.save(subject);
            logger.info("Subject updated successfully: {}", updated.getName());
            return updated;

        } catch (Exception e) {
            logger.error("Error updating subject with id {}", id, e);
            throw new RuntimeException("Failed to update subject with id: " + id + ". Error: " + e.getMessage());
        }
    }


    @Transactional
    public void deleteSubject(Long id) {
        try {
            subjectRepository.deleteById(id);
            logger.info("Subject deleted with id {}", id);
        } catch (Exception e) {
            logger.error("Error deleting subject with id {}", id, e);
            throw new RuntimeException("Failed to delete subject with id: " + id + ". Error: " + e.getMessage());
        }
    }

    public Subject getSubjectByName(String subjectName) {
        try {
            return subjectRepository.findByName(subjectName);
        } catch (Exception e) {
            logger.error("Error fetching subject by name: {}", subjectName, e);
            throw new RuntimeException("Failed to fetch subject by name: " + subjectName + ". Error: " + e.getMessage());
        }
    }

    public Subject getSubjectByDepartmentSemesterAndName(Department dept, int semester, String subjectName) {
        try {
            return subjectRepository.findByDepartmentAndSemesterAndName(dept, semester, subjectName);
        } catch (Exception e) {
            logger.error("Error fetching subject by department, semester and name", e);
            throw new RuntimeException("Failed to fetch subject. Error: " + e.getMessage());
        }
    }

    @Transactional
    public Map<String, Object> uploadSubjects(MultipartFile file) {
    	List<String> uploadedSubjects = new ArrayList<>();
        List<String> errors = new ArrayList<>();
        int rowNum = 0;

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                rowNum++;
                final int currentRow = rowNum;

                String[] fields = line.split(",");
                if (fields.length < 4) {
                    errors.add("Row " + currentRow + ": Missing fields. Expected 4, found " + fields.length);
                    continue;
                }

                String name = fields[0].trim();
                String code = fields[1].trim();
                String departmentName = fields[2].trim();
                String semesterStr = fields[3].trim();

                if (name.isEmpty() || code.isEmpty() || departmentName.isEmpty()) {
                    errors.add("Row " + currentRow + ": One or more required fields are empty.");
                    continue;
                }

                int semester;
                try {
                    semester = Integer.parseInt(semesterStr);
                } catch (NumberFormatException e) {
                    errors.add("Row " + currentRow + ": Invalid semester number: " + semesterStr);
                    continue;
                }

                


                Optional<Department> departmentOpt = departmentRepository.findDepartmentByName(departmentName);
                if (departmentOpt.isEmpty()) {
                    errors.add("Row " + currentRow + ": Department not found: " + departmentName);
                    continue;
                }
                
                if (subjectRepository.existsByNameAndDepartmentAndSemester(name, departmentOpt.get(), semester)) {
                    errors.add("Row " + currentRow + ": Duplicate subject name in same department and semester: " + name);
                    continue;
                }

                if (subjectRepository.existsByCodeAndDepartmentAndSemester(code, departmentOpt.get(), semester)) {
                    errors.add("Row " + currentRow + ": Duplicate subject code in same department and semester: " + code);
                    continue;
                }

                Subject subject = Subject.builder()
                        .name(name)
                        .code(code)
                        .department(departmentOpt.get())
                        .semester(semester)
                        .build();

                subjectRepository.save(subject);
                uploadedSubjects.add(name);
                logger.info("Subject uploaded from CSV: {}", code);
            }
        } catch (IOException e) {
            logger.error("CSV upload failed", e);
            errors.add("File read error: " + e.getMessage());
        }
        Map<String, Object> result = new HashMap<>();
        result.put("uploadedSubjects", uploadedSubjects);
        result.put("errors", errors);
        return result;
    }
}
