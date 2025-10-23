package com.copo.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.copo.app.model.Faculty;
import com.copo.app.model.Student;
import com.copo.app.repository.FacultyRepository;
import com.copo.app.repository.StudentRepository;

@Service
public class LoginService {
	
	@Autowired
    StudentRepository studentRepository;
	@Autowired
    FacultyRepository facultyRepository;
	
	@Autowired
	private FacultyService facultyService;


    public Student validateStudent(String userId, String dob) {
        return studentRepository.findByRollNumberAndDob(userId, dob).orElse(null);
    }

    public Faculty validateFaculty(String facultycode, String name) {
        return facultyRepository.findByFacultycodeAndName(facultycode, name).orElse(null);
    }
    
    /**
     * Validate faculty using faculty code and password (new secure method)
     * @param facultycode the faculty code
     * @param password the plain text password
     * @return the Faculty if credentials are valid, null otherwise
     */
    public Faculty validateFacultyWithPassword(String facultycode, String password) {
        return facultyService.verifyFacultyCredentials(facultycode, password);
    }
}