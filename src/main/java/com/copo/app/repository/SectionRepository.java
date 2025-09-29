package com.copo.app.repository;

import com.copo.app.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
	List<Section> findByDepartment_Id(Long departmentId);
	@Query("SELECT COUNT(s) FROM Section s WHERE s.department.id = :departmentId")
	long countByDepartment_Id(@Param("departmentId") Long departmentId);
}
