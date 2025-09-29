package com.copo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Department;
import com.copo.app.model.Subject;

@Repository
public interface SubjectRepository extends JpaRepository<Subject,Long> {

	Subject findByName(String subject);
	
	Subject findBycode(String subjectcode);

	boolean existsByCode(String subjectcode);
	
	List<Subject> findByDepartmentId(Long departmentId);
	List<Subject> findBySemester(int semester);
	List<Subject> findByDepartmentIdAndSemester(Long departmentId, int semester);

	 Subject findByDepartmentAndSemesterAndName(Department department, int semester, String name);

	 Optional<Subject> findByNameAndDepartmentAndSemester(String name, Department department, int semester);
	 Optional<Subject> findByCodeAndDepartmentAndSemester(String code, Department department, int semester);

	 
	boolean existsByName(String code);
	
	boolean existsByNameAndDepartmentAndSemester(String name, Department department, int semester);
	boolean existsByCodeAndDepartmentAndSemester(String code, Department department, int semester);

	@Query("SELECT COUNT(s) FROM Subject s WHERE s.department.id = :departmentId")
	long countByDepartmentId(@Param("departmentId") Long departmentId);

}
