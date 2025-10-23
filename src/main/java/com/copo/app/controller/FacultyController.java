package com.copo.app.controller;

import com.copo.app.model.Department;
import com.copo.app.model.Faculty;
import com.copo.app.service.DepartmentService;
import com.copo.app.service.FacultyService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private static final Logger logger = LoggerFactory.getLogger(FacultyController.class);

    @Autowired
    FacultyService facultyService;

    @Autowired
    DepartmentService departmentService;

    // Get all faculty members
    @GetMapping
    public String getAllFaculty(Model model) {
        try {
            List<Faculty> facultyList = facultyService.getAllFaculty();
            model.addAttribute("facultyList", facultyList);
            return "faculty/list";
        } catch (Exception e) {
            logger.error("Error fetching faculty list", e);
            model.addAttribute("error", "Unable to load faculty list. Error: "+e.getMessage());
            return "error";
        }
    }

    @GetMapping("/home")
    public String facultyHome(HttpSession session, Model model) {
        try {
            Faculty faculty = (Faculty) session.getAttribute("loggedDetails");
            if (faculty == null) {
                return "redirect:/login";
            }
            model.addAttribute("userDetails", faculty);
            return "login/facultydashboard";
        } catch (Exception e) {
            logger.error("Error loading faculty home", e);
            model.addAttribute("error", "Unable to load dashboard.Error: "+e.getMessage());
            return "error";
        }
    }

    // Show form for creating a new faculty member
    @GetMapping("/new")
    public String showCreateFacultyForm(Model model) {
        try {
            model.addAttribute("faculty", new Faculty());
            List<Department> departments = departmentService.getAllDepartments();
            model.addAttribute("departments", departments);
            return "faculty/create";
        } catch (Exception e) {
            logger.error("Error displaying create faculty form", e);
            model.addAttribute("error", "Unable to load form. Error: "+e.getMessage());
            return "error";
        }
    }

    // Save a new faculty member
    @PostMapping
    public String createFaculty(@ModelAttribute Faculty faculty, RedirectAttributes redirectAttributes) {
        try {
            facultyService.saveFaculty(faculty);
            redirectAttributes.addFlashAttribute("success", "Faculty created successfully!");
        } catch (Exception e) {
            logger.error("Error creating faculty", e);
            redirectAttributes.addFlashAttribute("error", "Failed to create faculty: Error: " + e.getMessage());
        }
        return "redirect:/faculty";
    }

    // Show form for editing a faculty member
    @GetMapping("/edit/{id}")
    public String showEditFacultyForm(@PathVariable Long id, Model model) {
        try {
            Faculty faculty = facultyService.getFacultyById(id)
                    .orElseThrow(() -> new RuntimeException("Faculty not found with ID: " + id));
            model.addAttribute("faculty", faculty);
            List<Department> departments = departmentService.getAllDepartments();
            model.addAttribute("departments", departments);
            return "faculty/edit";
        } catch (Exception e) {
            logger.error("Error loading edit form for faculty ID: {}", id, e);
            model.addAttribute("error", "Unable to load edit form. Error: "+e.getMessage());
            return "error";
        }
    }

    // Update an existing faculty member
    @PostMapping("/edit/{id}")
    public String updateFaculty(@PathVariable Long id, @ModelAttribute Faculty faculty, RedirectAttributes redirectAttributes) {
        try {
            facultyService.updateFaculty(id, faculty);
            redirectAttributes.addFlashAttribute("success", "Faculty updated successfully!");
        } catch (Exception e) {
            logger.error("Error updating faculty with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Failed to update faculty: Erro : " + e.getMessage());
        }
        return "redirect:/faculty";
    }

    // Delete a faculty member
    @GetMapping("/delete/{id}")
    public String deleteFaculty(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facultyService.deleteFaculty(id);
            redirectAttributes.addFlashAttribute("success", "Faculty deleted successfully!");
        } catch (Exception e) {
            logger.error("Error deleting faculty with ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Failed to delete faculty: Error : " + e.getMessage());
        }
        return "redirect:/faculty";
    }

    // Upload faculty via CSV
    @PostMapping("/upload-csv")
    @SuppressWarnings("unchecked")
    public String uploadFacultyCSV(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {

            Map<String, Object> result = facultyService.uploadFaculty(file);

            List<String> uploadedfaculties = (List<String>) result.get("uploadedFaculties");
            List<String> errors = (List<String>) result.get("errors");

            if (!errors.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", String.join(" : : ", errors));
                logger.warn("Faculties upload completed with errors");
                if (!uploadedfaculties.isEmpty()) {
                    redirectAttributes.addFlashAttribute("success", "Few Uploaded successfully" + uploadedfaculties);
                }
            } else {
                redirectAttributes.addFlashAttribute("success", "Faculty data uploaded successfully!");
                logger.info("faculties uploaded successfully via CSV");
            }

        } catch (Exception e) {
            logger.error("Error uploading faculty CSV", e);
            redirectAttributes.addFlashAttribute("error", "Error uploading faculty data: " + e.getMessage());
        }
        return "redirect:/faculty";
    }

    // Initialize default passwords for all faculty
    @GetMapping("/init-passwords")
    public String initializeDefaultPasswords(RedirectAttributes redirectAttributes) {
        try {
            facultyService.setDefaultPasswordForAllFaculty();
            redirectAttributes.addFlashAttribute("success", "Default passwords (5106) have been set for all faculty members!");
            logger.info("Default passwords initialized for all faculty");
        } catch (Exception e) {
            logger.error("Error initializing default passwords", e);
            redirectAttributes.addFlashAttribute("error", "Failed to initialize default passwords: " + e.getMessage());
        }
        return "redirect:/faculty";
    }

    // Reset password to default for a specific faculty
    @GetMapping("/reset-password/{id}")
    public String resetPasswordToDefault(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            boolean success = facultyService.resetFacultyPasswordToDefault(id);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "Password reset to default (5106) for faculty ID: " + id);
            } else {
                redirectAttributes.addFlashAttribute("error", "Faculty not found with ID: " + id);
            }
        } catch (Exception e) {
            logger.error("Error resetting password for faculty ID: {}", id, e);
            redirectAttributes.addFlashAttribute("error", "Failed to reset password: " + e.getMessage());
        }
        return "redirect:/faculty";
    }

    // Debug endpoint to check faculty data and set default passwords
    @GetMapping("/debug-setup")
    public String debugFacultySetup(RedirectAttributes redirectAttributes) {
        try {
            List<Faculty> allFaculty = facultyService.getAllFaculty();
            StringBuilder debugInfo = new StringBuilder();
            debugInfo.append("Total Faculty: ").append(allFaculty.size()).append("\n");
            
            int updatedCount = 0;
            for (Faculty faculty : allFaculty) {
                debugInfo.append("Faculty: ").append(faculty.getFacultycode())
                        .append(" - ").append(faculty.getName())
                        .append(" - Password: ").append(
                            faculty.getPassword() != null && !faculty.getPassword().isEmpty() 
                            ? "SET" : "NOT SET"
                        ).append("\n");
                
                // Set password if not set
                if (faculty.getPassword() == null || faculty.getPassword().trim().isEmpty()) {
                    boolean success = facultyService.setFacultyPassword(faculty.getId(), "5106");
                    if (success) {
                        updatedCount++;
                        debugInfo.append("  -> Password set to 5106\n");
                    }
                }
            }
            
            debugInfo.append("\nUpdated ").append(updatedCount).append(" faculty with password 5106");
            
            redirectAttributes.addFlashAttribute("success", "Debug completed: " + debugInfo.toString());
            logger.info("Debug setup completed. Updated {} faculty members.", updatedCount);
        } catch (Exception e) {
            logger.error("Error in debug setup", e);
            redirectAttributes.addFlashAttribute("error", "Debug setup failed: " + e.getMessage());
        }
        return "redirect:/faculty";
    }

}
