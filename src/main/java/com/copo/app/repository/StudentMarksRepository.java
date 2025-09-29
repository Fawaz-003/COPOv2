package com.copo.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Question;
import com.copo.app.model.Student;
import com.copo.app.model.StudentMarks;

import java.util.List;

@Repository
public interface StudentMarksRepository extends JpaRepository<StudentMarks, Long> {
    
    List<StudentMarks> findByStudent(Student student);
    
    
    List<StudentMarks> findByStudent_IdInAndQuestion_IdIn(List<Long> studentIds, List<Long> questionIds);

    
    
    @Query("SELECT sm.id AS id, sm.student.id AS studentId, sm.question.id AS questionId, sm.answer AS answer FROM StudentMarks sm "
            + "JOIN sm.question q "
            + "WHERE sm.student.id = :studentId "
            + "AND q.department.id = :departmentId "
            + "AND q.batch.id = :batchId "
            + "AND q.semester = :semester "
            + "AND q.examType = :examType "
            + "AND q.subject.id = :subjectId")
       List<StudentMarksProjection> findMarksByStudentIdAndCriteria(
           @Param("studentId") Long studentId,
           @Param("departmentId") Long departmentId,
           @Param("batchId") Long batchId,
           @Param("semester") int semester,
           @Param("examType") String examType,
           @Param("subjectId") Long subjectId);
    

    
}
