package com.copo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Student;

@Repository
public interface StudentRepository extends JpaRepository<Student,Long> {
	
	
    boolean existsByRegisterNumber(String registerNumber);
	
	Optional<Student> findByRollNumberAndDob(String userId, String dob);
    List<Student> findByDepartmentIdAndBatchIdOrderByRollNumberAsc(Long departmentId, Long batchId);
	
	// Find all students by department, batch, semester, and exam type
    @Query("SELECT s FROM Student s " +
            "WHERE s.department.id = :departmentId " +
            "AND s.batch.id = :batchId ")
    List<Student> findAllByDepartmentBatchAndSemester(Long departmentId, Long batchId);
    
    List<Student> findByDepartmentIdOrderByRollNumberAsc(Long departmentId);

    List<Student> findByBatchIdOrderByRollNumberAsc(Long batchId);

    List<Student> findAllByOrderByRollNumberAsc();
    
    List<Student> findByDepartmentIdAndBatchIdAndSection_IdOrderByRollNumberAsc(Long departmentId, Long batchId, Integer sectionId);


    boolean existsByRollNumber(String rollNumber);

	Optional<Student> findByRollNumber(String rollNumber);

	Optional<Student> findByRegisterNumber(String registerNumber);

	@Query("SELECT COUNT(s) FROM Student s WHERE s.department.id = :departmentId")
	long countByDepartmentId(@Param("departmentId") Long departmentId);

}
