package com.copo.app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

	Optional<Department> findDepartmentByName(String department);
	
	// Find only non-deleted departments
	@Query("SELECT d FROM Department d WHERE d.isDeleted = false")
	List<Department> findAllActive();
	
	// Find department by name (only non-deleted)
	@Query("SELECT d FROM Department d WHERE d.name = :name AND d.isDeleted = false")
	Optional<Department> findActiveDepartmentByName(@Param("name") String name);
	
	// Soft delete - mark as deleted
	@Modifying
	@Query("UPDATE Department d SET d.isDeleted = true WHERE d.id = :id")
	void softDeleteById(@Param("id") Long id);
	
}
