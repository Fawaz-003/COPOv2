package com.copo.app.controller;

import com.copo.app.model.Faculty;
import com.copo.app.service.FacultyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller for setup and debugging purposes
 * This controller should be removed or secured in production
 */
@Controller
@RequestMapping("/setup")
public class SetupController {

    private static final Logger logger = LoggerFactory.getLogger(SetupController.class);

    @Autowired
    private FacultyService facultyService;

    /**
     * Setup default passwords for all faculty - NO AUTHENTICATION REQUIRED
     * This is a temporary endpoint for setup purposes
     */
    @GetMapping("/faculty-passwords")
    public String setupFacultyPasswords(Model model) {
        try {
            List<Faculty> allFaculty = facultyService.getAllFaculty();
            int updatedCount = 0;
            
            StringBuilder result = new StringBuilder();
            result.append("=== FACULTY PASSWORD SETUP ===\n");
            result.append("Total Faculty Found: ").append(allFaculty.size()).append("\n\n");
            
            for (Faculty faculty : allFaculty) {
                result.append("Faculty Code: ").append(faculty.getFacultycode())
                      .append(" | Name: ").append(faculty.getName())
                      .append(" | Password Status: ");
                
                if (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty()) {
                    result.append("NOT SET");
                    // Set password to 5106
                    boolean success = facultyService.setFacultyPassword(faculty.getId(), "5106");
                    if (success) {
                        updatedCount++;
                        result.append(" -> NOW SET TO 5106");
                    } else {
                        result.append(" -> FAILED TO SET");
                    }
                } else {
                    result.append("ALREADY SET");
                }
                result.append("\n");
            }
            
            result.append("\n=== SUMMARY ===\n");
            result.append("Updated ").append(updatedCount).append(" faculty members with password: 5106\n");
            result.append("All faculty can now login with their faculty code + password: 5106\n");
            
            model.addAttribute("setupResult", result.toString());
            model.addAttribute("success", true);
            
            logger.info("Faculty password setup completed. Updated {} faculty members.", updatedCount);
            
        } catch (Exception e) {
            logger.error("Error setting up faculty passwords", e);
            model.addAttribute("setupResult", "ERROR: " + e.getMessage());
            model.addAttribute("success", false);
        }
        
        return "setup/result";
    }

    /**
     * Check faculty login credentials
     */
    @GetMapping("/test-login")
    public String testFacultyLogin(Model model) {
        try {
            List<Faculty> allFaculty = facultyService.getAllFaculty();
            StringBuilder result = new StringBuilder();
            
            result.append("=== FACULTY LOGIN TEST ===\n");
            result.append("Total Faculty: ").append(allFaculty.size()).append("\n\n");
            
            result.append("To test login, use these credentials:\n");
            for (Faculty faculty : allFaculty) {
                result.append("Faculty Code: ").append(faculty.getFacultycode())
                      .append(" | Password: 5106")
                      .append(" | Name: ").append(faculty.getName()).append("\n");
            }
            
            result.append("\nGo to: http://localhost:9091/login\n");
            result.append("Click Faculty tab and use any faculty code above with password: 5106");
            
            model.addAttribute("setupResult", result.toString());
            model.addAttribute("success", true);
            
        } catch (Exception e) {
            logger.error("Error testing faculty login", e);
            model.addAttribute("setupResult", "ERROR: " + e.getMessage());
            model.addAttribute("success", false);
        }
        
        return "setup/result";
    }
}






