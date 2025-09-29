package com.copo.app.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Question;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

	List<Question> findBySubjectIdAndExamType(Long subjectId, String examType);
    // Custom query methods can be added here if needed
	List<Question> findByDepartmentId(Long departmentId);
	
	List<Question> findBySubjectIdAndSemesterAndExamType(Long subjectId, int semester, String examType);

	
	/*
	 * @Query("SELECT q FROM Question q " + "JOIN q.department d " +
	 * "JOIN q.batch b " + "JOIN q.subject s " + "WHERE d.name = :department " +
	 * "AND b.name = :batch " + "AND q.semester = :semester " +
	 * "AND q.examType = :examType " + "AND s.name = :subject") List<Question>
	 * findQuestions(
	 * 
	 * @Param("department") String department,
	 * 
	 * @Param("batch") String batch,
	 * 
	 * @Param("semester") int semester,
	 * 
	 * @Param("examType") String examType,
	 * 
	 * @Param("subject") String subject );
	 */
	
	@Query("SELECT q FROM Question q "
		     + "JOIN FETCH q.department d "
		     + "JOIN FETCH q.batch b "
		     + "JOIN FETCH q.subject s "
		     + "WHERE LOWER(d.name) = LOWER(:department) "
		     + "AND LOWER(b.name) = LOWER(:batch) "
		     + "AND q.semester = :semester "
		     + "AND LOWER(q.examType) = LOWER(:examType) "
		     + "AND LOWER(s.name) = LOWER(:subject)")
		List<Question> findQuestions(
		    @Param("department") String department,
		    @Param("batch") String batch,
		    @Param("semester") int semester,
		    @Param("examType") String examType,
		    @Param("subject") String subject
		);


	
	
	@Query("SELECT q FROM Question q WHERE q.department = :department AND q.semester = :semester AND q.subject = :subject AND q.examType = :examType"
		     + " AND (:batch IS NULL OR q.batch = :batch)")
		List<Question> findByFilters(@Param("department") String department,
		                             @Param("batch") String batch,
		                             @Param("semester") int semester,
		                             @Param("subject") String subject,
		                             @Param("examType") String examType);

	@Query("SELECT COUNT(q) FROM Question q WHERE q.department.id = :departmentId")
	long countByDepartmentId(@Param("departmentId") Long departmentId);

}

