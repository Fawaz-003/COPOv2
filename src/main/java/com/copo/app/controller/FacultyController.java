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

}
