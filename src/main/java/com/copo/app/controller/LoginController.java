package com.copo.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.copo.app.model.Faculty;
import com.copo.app.model.Student;
import com.copo.app.service.BatchService;
import com.copo.app.service.DepartmentService;
import com.copo.app.service.LoginService;
import com.copo.app.service.StudentService;

import jakarta.servlet.http.HttpSession;



@Controller
@RequestMapping("/login")
public class LoginController {
    
	@Autowired
	LoginService loginService;
	
	@Autowired
	StudentService studentService;
	@Autowired
	DepartmentService departmentService;
	@Autowired
	BatchService batchService;
	
	@GetMapping
    public String loginPage(@RequestParam(value = "error", required = false) String error, 
                            @RequestParam(value = "logout", required = false) String logout, 
                            Model model) {
        if (error != null) {
            model.addAttribute("error", "Invalid credentials! Please try again.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "You have successfully logged out.");
        }

        
        
        return "login/login"; // Loads templates/login.html
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Clears the session
        return "redirect:/login?logout=true"; // Redirects to login page with logout message
    }
    

	
    @PostMapping("/students")
    public String studentLogin(@RequestParam String rollNumber,
                               @RequestParam  String dob,
                               HttpSession session,
                               Model model) {
    	Student student = loginService.validateStudent(rollNumber, dob);
    	
    	System.out.println("Student details ==> "+student);
        if (student != null) {
        	session.setAttribute("loggedInPerson", student.getName());
        	session.setAttribute("role", "student");
        	session.setAttribute("loggedDetails", student);
        	model.addAttribute("userDetails", student);
            // Redirect to the student dashboard (or any success page)
        	return "redirect:/students/home";
        } else {
            // Add error message to the model
            model.addAttribute("error", "Invalid Student RollNo or Date of Birth!");
            return "login/login"; // Return to login.html
        }
    }
    
    

    @PostMapping("/faculties")
    public String facultyLogin(@RequestParam String facultycode,
                               @RequestParam String name,
                               HttpSession session,
                               Model model) {
    	
    	Faculty faculty = loginService.validateFaculty(facultycode, name);
    	
    	System.out.println("login faculty "+faculty);
        if (faculty != null) {
        	System.out.println("inside login faculty ");
        	session.setAttribute("loggedInPerson", faculty.getName());
        	session.setAttribute("role", "faculty");
        	session.setAttribute("loggedDetails", faculty);
        	model.addAttribute("userDetails", faculty);
            // Redirect to the faculty dashboard (or any success page)
        	return "redirect:/faculty/home";
        } else {
            // Add error message to the model
            model.addAttribute("error", "Invalid Faculty Name or Faculty Code!");
            return "login/login"; // Return to login.html
        }
    }
}
