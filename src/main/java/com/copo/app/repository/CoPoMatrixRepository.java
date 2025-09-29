package com.copo.app.repository;

import com.copo.app.model.CoPoMatrixEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CoPoMatrixRepository extends JpaRepository<CoPoMatrixEntry, Long> {
    List<com.copo.app.model.CoPoMatrixEntry> findBySubjectCode(String subjectCode);

    
    List<com.copo.app.model.CoPoMatrixEntry>  findBySubjectName(String subjectName);
    
    List<CoPoMatrixEntry> findBySubjectNameAndSubjectCode(String subjectName, String subjectCode);
}