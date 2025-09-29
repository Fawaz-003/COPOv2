package com.copo.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Faculty;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {
	
	Optional<Faculty> findByFacultycodeAndName(String facultycode, String name);
	
	boolean existsByFacultycode(String facultycode);

	Optional<Faculty> findByFacultycode(String facultycode);


	
	//Optional<Faculty> findByNameAndEmail(String name, String email);
    
	@Query("SELECT COUNT(f) FROM Faculty f WHERE f.department.id = :departmentId")
	long countByDepartmentId(@Param("departmentId") Long departmentId);
}