package com.copo.app.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.copo.app.model.Student;
import com.copo.app.model.StudentMarks;
import com.copo.app.repository.StudentMarksProjection;
import com.copo.app.repository.StudentMarksRepository;

@Service
public class StudentMarksService {

    @Autowired
    private StudentMarksRepository studentMarksRepository;
    

    
    public List<StudentMarksProjection> getMarksByStudentIdAndCriteria(Long studentId, Long departmentId, Long batchId, int semester, String examType, Long subjectId) {
        // Call the repository to fetch marks based on criteria
        return studentMarksRepository.findMarksByStudentIdAndCriteria(studentId, departmentId, batchId, semester, examType, subjectId);
    }


    public List<StudentMarks> getMarksByStudent(Student student) {
        return studentMarksRepository.findByStudent(student);
    }

    public void saveStudentMarks(List<StudentMarks> marks) {
        studentMarksRepository.saveAll(marks);
    }
}
