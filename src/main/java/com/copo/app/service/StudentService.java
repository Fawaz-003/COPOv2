package com.copo.app.service;

import com.copo.app.model.Batch;
import com.copo.app.model.Department;
import com.copo.app.model.Student;
import com.copo.app.repository.BatchRepository;
import com.copo.app.repository.DepartmentRepository;
import com.copo.app.repository.StudentRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class StudentService {
    /**
     * Fetch students filtered by department, batch, and section id.
     */
    public List<Student> getStudentsByDepartmentBatchAndSectionId(Long departmentId, Long batchId, Long sectionId) {
        try {
            if (departmentId != null && batchId != null && sectionId != null) {
                logger.info("Fetching students with departmentId={}, batchId={}, sectionId={}", departmentId, batchId, sectionId);
                return studentRepository.findByDepartmentIdAndBatchIdAndSection_IdOrderByRollNumberAsc(departmentId, batchId, sectionId.intValue());
            } else {
                // fallback to batch/department filter only
                return getStudentsByDepartmentAndBatch(departmentId, batchId);
            }
        } catch (Exception e) {
            logger.error("Error fetching students with departmentId={}, batchId={}, sectionId={}", departmentId, batchId, sectionId, e);
            throw new RuntimeException("Failed to fetch students. Error: " + e.getMessage());
        }
    }

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private BatchRepository batchRepository;

    /**
     * Save a new student with validation and logging.
     */
    @Transactional
    public Student saveStudent(Student student) {
        try {
            if (student == null) {
                logger.error("Attempted to save a null student object.");
                throw new IllegalArgumentException("Student data must not be null");
            }

            // Validate mandatory fields
            if (student.getName() == null || student.getName().isBlank()) {
                logger.warn("Student name is null or blank during save.");
                throw new IllegalArgumentException("Student name cannot be null or blank");
            }

            if (student.getRollNumber() == null || student.getRollNumber().isBlank()) {
                logger.warn("Student roll number is null or blank during save.");
                throw new IllegalArgumentException("Roll number cannot be null or blank");
            }

            if (student.getRegisterNumber() == null || student.getRegisterNumber().isBlank()) {
                logger.warn("Student register number is null or blank during save.");
                throw new IllegalArgumentException("Register number cannot be null or blank");
            }

            // Check for duplicates
            if (studentRepository.existsByRollNumber(student.getRollNumber())) {
                logger.warn("Duplicate roll number '{}' detected during save.", student.getRollNumber());
                throw new RuntimeException("Student already exists with roll number: " + student.getRollNumber());
            }

            if (studentRepository.existsByRegisterNumber(student.getRegisterNumber())) {
                logger.warn("Duplicate register number '{}' detected during save.", student.getRegisterNumber());
                throw new RuntimeException("Student already exists with register number: " + student.getRegisterNumber());
            }

            // Save student (section is already set by controller/model binding)
            Student saved = studentRepository.save(student);
            logger.info("Student saved successfully: {}", saved.getRollNumber());
            return saved;

        } catch (Exception e) {
            logger.error("Error saving student with roll number={} and register number={}",
                         student != null ? student.getRollNumber() : "null",
                         student != null ? student.getRegisterNumber() : "null", e);
            throw new RuntimeException("Failed to save student. Error: " + e.getMessage());
        }
    }

    // Get all students
    public List<Student> getAllStudents() {
        try {
            List<Student> list = studentRepository.findAllByOrderByRollNumberAsc();
            logger.info("Fetched all students records. Total: {}", list.size());
            return list;
        }catch(Exception e) {
            logger.error("Error fetching all students", e);
            throw new RuntimeException("Failed to retrieve students list. Error: " + e.getMessage());
        }
    }

    // Get a student by ID
    public Optional<Student> getStudentById(Long id) {
    	try {
            Optional<Student> student = studentRepository.findById(id);
            if (student.isPresent()) {
                logger.info("student fetched with id: {}", id);
            } else {
                logger.warn("student not found with id: {}", id);
            }
            return student;
        } catch (Exception e) {
            logger.error("Error fetching student by id: {}", id, e);
            throw new RuntimeException("Failed to get student by ID. Error: " + e.getMessage());
        }
       
    }
    
    /**
     * Update student after checking for duplicate roll number and register number.
     */
    public Student updateStudent(Long id, Student studentDetails) {
        try {
            Student student = studentRepository.findById(id)
                    .orElseThrow(() -> {
                        logger.warn("Student not found for update with id: {}", id);
                        return new RuntimeException("Student not found with id: " + id);
                    });
            
            // Check roll number duplication only if it changed
            if (!student.getRollNumber().equals(studentDetails.getRollNumber())) {
                Optional<Student> duplicateRoll = studentRepository.findByRollNumber(studentDetails.getRollNumber());
                if (duplicateRoll.isPresent() && !duplicateRoll.get().getId().equals(id)) {
                    logger.warn("Duplicate roll number '{}' during update", studentDetails.getRollNumber());
                    throw new RuntimeException("Roll number already exists: " + studentDetails.getRollNumber());
                }
            }

            // Check register number duplication only if it changed
            if (!student.getRegisterNumber().equals(studentDetails.getRegisterNumber())) {
                Optional<Student> duplicateRegister = studentRepository.findByRegisterNumber(studentDetails.getRegisterNumber());
                if (duplicateRegister.isPresent() && !duplicateRegister.get().getId().equals(id)) {
                    logger.warn("Duplicate register number '{}' during update", studentDetails.getRegisterNumber());
                    throw new RuntimeException("Register number already exists: " + studentDetails.getRegisterNumber());
                }
            }

            // Trim and update values
            student.setName(studentDetails.getName().trim());
            student.setRollNumber(studentDetails.getRollNumber().trim());
            student.setRegisterNumber(studentDetails.getRegisterNumber().trim());
            student.setDob(studentDetails.getDob());
            student.setDepartment(studentDetails.getDepartment());
            student.setBatch(studentDetails.getBatch());
            student.setSection(studentDetails.getSection());

            Student updated = studentRepository.save(student);
            logger.info("Student updated: {}", updated.getRollNumber());
            return updated;

        } catch (Exception e) {
            logger.error("Error updating student with id {}", id, e);
            throw new RuntimeException("Failed to update student. Error: " + e.getMessage());
        }
    }


    // Delete a student by ID
    @Transactional
    public void deleteStudent(Long id) {
    	try {
    		if (!studentRepository.existsById(id)) {
                logger.warn("Student not found for deletion: ID {}", id);
                throw new RuntimeException("Student not found with ID: " + id);
            }
            studentRepository.deleteById(id);
            logger.info("Student deleted successfully with ID: {}", id);

        } catch (Exception e) {
            logger.error("Error deleting Student with id: {}", id, e);
            throw new RuntimeException("Failed to delete Student. Error: " + e.getMessage());
        }
        
    }

    // Bulk upload students from CSV file
    @Transactional
    public Map<String, Object> uploadStudents(MultipartFile file) throws Exception {
        logger.info("Starting student upload from file: {}", file.getOriginalFilename());
        
        List<String> uploaded = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;

            while ((line = br.readLine()) != null) {
                lineNumber++;
                final int currentRow = lineNumber;
                String[] fields = line.split(",");

                if (fields.length < 6) {
                    logger.warn("Skipping line {} due to incorrect format: {}", lineNumber, line);
                    continue;
                }

                String name = fields[0].trim();
                String rollNumber = fields[1].trim();
                String registerNumber = fields[2].trim();
                String dob = fields[3].trim();
                String departmentName = fields[4].trim();
                String batchName = fields[5].trim();
                
             // Validate for empty fields
                if (name.isEmpty() || rollNumber.isEmpty() || registerNumber.isEmpty() ||
                    dob.isEmpty() || departmentName.isEmpty() || batchName.isEmpty()) {
                    logger.warn("Row {} has empty fields. Skipping.", currentRow);
                    errors.add("Row " + currentRow + ": One or more required fields are empty.");
                    continue;
                }

                try {
                	
                	
                    if (studentRepository.existsByRollNumber(rollNumber)) {
                        logger.info("Skipping duplicate roll number at line {}: {}", lineNumber, rollNumber);
                        errors.add("Row " + currentRow + ": Duplicate student rollNumber: " + rollNumber);
                        continue;
                    }
                    if (studentRepository.existsByRegisterNumber(registerNumber)) {
                        logger.info("Skipping duplicate roll number at line {}: {}", lineNumber, registerNumber);
                        errors.add("Row " + currentRow + ": Duplicate student registerNumber: " + registerNumber);
                        continue;
                    }

                    Department department = departmentRepository.findDepartmentByName(departmentName)
                            .orElseThrow(() -> new RuntimeException("Department not found: " + departmentName));

                    Batch batch = batchRepository.findBatchByName(batchName)
                            .orElseThrow(() -> new RuntimeException("Batch not found: " + batchName));

                    Student student = Student.builder()
                            .name(name)
                            .rollNumber(rollNumber)
                            .registerNumber(registerNumber)
                            .dob(dob)
                            .department(department)
                            .batch(batch)
                            .build();

                    studentRepository.save(student);
                    uploaded.add("Student Code : "+rollNumber +" : : Student name :"+name);
                    logger.info("Row {}: Student uploaded: {}", currentRow, rollNumber);
                    

                } catch (Exception e) {
                    logger.error("Row {} failed to upload: {}", currentRow, e.getMessage());
                    errors.add("Row " + currentRow + ": " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to process student file upload: {}", ex.getMessage());
            throw new RuntimeException("Failed to upload students: " + ex.getMessage(), ex);
        }

        logger.info("student upload completed. Success: {}, Errors: {}", uploaded.size(), errors.size());

        Map<String, Object> result = new HashMap<>();
        result.put("uploadedStudents", uploaded);
        result.put("errors", errors);
        return result;
    }

    /**
     * Fetch students filtered by department and batch.
     */
    public List<Student> getStudentsByDepartmentAndBatch(Long departmentId, Long batchId) {
        try {
            if (departmentId != null && batchId != null) {
                logger.info("Fetching students with departmentId={} and batchId={}", departmentId, batchId);
                return studentRepository.findByDepartmentIdAndBatchIdOrderByRollNumberAsc(departmentId, batchId);
            } else if (departmentId != null) {
                logger.info("Fetching students with departmentId={}", departmentId);
                return studentRepository.findByDepartmentIdOrderByRollNumberAsc(departmentId);
            } else if (batchId != null) {
                logger.info("Fetching students with batchId={}", batchId);
                return studentRepository.findByBatchIdOrderByRollNumberAsc(batchId);
            } else {
                logger.info("Fetching all students (no department or batch filter applied)");
                return studentRepository.findAllByOrderByRollNumberAsc();
            }
        } catch (Exception e) {
            logger.error("Error fetching students with departmentId={} and batchId={}", departmentId, batchId, e);
            throw new RuntimeException("Failed to fetch students. Error: " + e.getMessage());
        }
    }
    
    /**
     * Fetch students filtered by department, batch, and section ID.
     */
    public List<Student> getStudentsByDepartmentBatchAndSection(Long departmentId, Long batchId, Long sectionId) {
        try {
            if (departmentId != null && batchId != null && sectionId != null) {
                logger.info("Fetching students with departmentId={}, batchId={}, sectionId={}", departmentId, batchId, sectionId);
                return studentRepository.findByDepartmentIdAndBatchIdAndSection_IdOrderByRollNumberAsc(departmentId, batchId, sectionId.intValue());
            } else {
                // fallback to batch/department filter only
                return getStudentsByDepartmentAndBatch(departmentId, batchId);
            }
        } catch (Exception e) {
            logger.error("Error fetching students with departmentId={}, batchId={}, sectionId={}", departmentId, batchId, sectionId, e);
            throw new RuntimeException("Failed to fetch students. Error: " + e.getMessage());
        }
    }

    // Check if roll number exists
    public boolean existsByRollNumber(String rollNumber) {
        try {
            logger.info("Checking if roll number exists: {}", rollNumber);
            return studentRepository.existsByRollNumber(rollNumber);
        } catch (Exception e) {
            logger.error("Error checking roll number existence: {}", rollNumber, e);
            throw new RuntimeException("Failed to check roll number existence", e);
        }
    }

    // Check if register number exists
    public boolean existsByRegisterNumber(String registerNumber) {
        try {
            logger.info("Checking if register number exists: {}", registerNumber);
            return studentRepository.existsByRegisterNumber(registerNumber);
        } catch (Exception e) {
            logger.error("Error checking register number existence: {}", registerNumber, e);
            throw new RuntimeException("Failed to check register number existence", e);
        }
    }

}
