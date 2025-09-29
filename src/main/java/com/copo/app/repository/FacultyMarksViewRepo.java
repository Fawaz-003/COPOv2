package com.copo.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.StudentMarks;

@Repository
public interface FacultyMarksViewRepo extends JpaRepository<StudentMarks, Long> {
    @Query("SELECT s.id AS studentId, s.name AS studentName, q.id AS questionId, " +
           "q.part AS part, q.questionNumber AS questionNumber, q.text AS questionText, " +
           "q.maxMarks AS maxMarks, q.courseOutcome AS courseOutcome, sm.answer AS submittedMarks " +
           "FROM Student s " +
           "CROSS JOIN Question q " +
           "LEFT JOIN StudentMarks sm ON s.id = sm.student.id AND q.id = sm.question.id " +
           "WHERE s.department.id = :departmentId AND s.batch.id = :batchId " +
           "AND q.department.id = :departmentId AND q.batch.id = :batchId AND q.semester = :semester " +
           "AND q.examType = :examType AND q.subject.id = :subjectId " +
           "ORDER BY s.name, q.part, q.questionNumber")
    List<FacultyMarksViewProjection> getFilteredMarks(
        @Param("departmentId") Long departmentId,
        @Param("batchId") Long batchId,
        @Param("semester") Integer semester,
        @Param("examType") String examType,
        @Param("subjectId") Long subjectId
    );

    @Query("SELECT s.id AS studentId, s.name AS studentName, q.id AS questionId, " +
           "q.part AS part, q.questionNumber AS questionNumber, q.text AS questionText, " +
           "q.maxMarks AS maxMarks, q.courseOutcome AS courseOutcome, sm.answer AS submittedMarks " +
           "FROM Student s " +
           "CROSS JOIN Question q " +
           "LEFT JOIN StudentMarks sm ON s.id = sm.student.id AND q.id = sm.question.id " +
           "WHERE s.department.id = :departmentId AND s.batch.id = :batchId AND s.section.id = :sectionId " +
           "AND q.department.id = :departmentId AND q.batch.id = :batchId AND q.semester = :semester " +
           "AND q.examType = :examType AND q.subject.id = :subjectId " +
           "ORDER BY s.name, q.part, q.questionNumber")
    List<FacultyMarksViewProjection> getFilteredMarksBySection(
        @Param("departmentId") Long departmentId,
        @Param("batchId") Long batchId,
        @Param("semester") Integer semester,
        @Param("examType") String examType,
        @Param("subjectId") Long subjectId,
        @Param("sectionId") Integer sectionId
    );
    
    
    
    
    
    @Query("SELECT s.id AS studentId, s.name AS studentName, s.rollNumber AS rollNumber, q.id AS questionId, " +
    	       "q.part AS part, q.questionNumber AS questionNumber, q.text AS questionText, " +
    	       "q.maxMarks AS maxMarks, q.courseOutcome AS courseOutcome, q.examType AS examType, " +  // Include examType!
    	       "sm.answer AS submittedMarks " +
    	       "FROM Student s " +
    	       "CROSS JOIN Question q " +
    	       "LEFT JOIN StudentMarks sm ON s.id = sm.student.id AND q.id = sm.question.id " +
    	       "WHERE s.department.id = :departmentId AND s.batch.id = :batchId " +
    	       " AND q.department.id = :departmentId AND q.batch.id = :batchId AND q.semester = :semester " +
    	       "AND q.subject.id = :subjectId " +
    	       "ORDER BY s.name, q.courseOutcome, q.examType, q.part, q.questionNumber")
    	List<FacultyMarksViewProjection> getFullMarksWithoutExamType(
    	    @Param("departmentId") Long departmentId,
    	    @Param("batchId") Long batchId,
    	    @Param("semester") Integer semester,
    	    @Param("subjectId") Long subjectId
    	);

    
    
    
    
}


