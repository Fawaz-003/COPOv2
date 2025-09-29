package com.copo.app.service;

import com.copo.app.model.Section;
import com.copo.app.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {
    @Autowired
    private SectionRepository sectionRepository;

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public List<Section> getSectionsByDepartmentId(Long departmentId) {
        return sectionRepository.findByDepartment_Id(departmentId);
    }
}
