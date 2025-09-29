package com.copo.app.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.copo.app.model.Batch;
import com.copo.app.model.Department;
import com.copo.app.model.Question;
import com.copo.app.model.Subject;
import com.copo.app.repository.QuestionRepository;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class QuestionService {

	@Autowired
	QuestionRepository questionRepository;
	
	@Autowired
    SubjectService subjectService;
	@Autowired
    DepartmentService departmentService;
	@Autowired
    BatchService batchService;
	
	// ✅ Get questions based on user input
        public List<Question> getFilteredQuestions(String department, String batch, int semester, String examType, String subject) {
        try {
            return questionRepository.findQuestions(department, batch, semester, examType, subject);
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving questions: " + e.getMessage());
        }
    }


    public void saveAllQuestions(List<Question> questions) {
        try {
            questionRepository.saveAll(questions);
        } catch (Exception e) {
            throw new RuntimeException("Error saving questions: " + e.getMessage());
        }
    }

    public List<Question> getAllQuestions() {
        try {
            return questionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving all questions: " + e.getMessage());
        }
    }

    // Delete a question by ID
    @Transactional
    public void deleteQuestion(Long id) {
        try {
            questionRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting question: " + e.getMessage());
        }
    }
    
    @Transactional
    public void deleteQuestionsByFilters(String department, String batch, int semester, String subject, String examType) {
        try {
            List<Question> questions = questionRepository.findByFilters(department, batch, semester, subject, examType);
            if (questions.isEmpty()) {
                log.warn("No questions found for the given filters. Nothing to delete.");
            } else {
                questionRepository.deleteAll(questions);
                log.info("Deleted {} questions for the given filters.", questions.size());
            }
        } catch (Exception ex) {
            log.error("Exception in deleteQuestionsByFilters: ", ex);
            throw ex; // rethrow to be handled in controller
        }
    }
    
    
 // Retrieve a question by ID
    public Question getQuestionById(Long id) {
        try {
            return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with ID: " + id));
        } catch (Exception e) {
            throw new RuntimeException("Error retrieving question with ID " + id + ": " + e.getMessage());
        }
    }

    // Update and save a question
    @Transactional
    public Question updateQuestion(Long id, Question updatedQuestion) {
        try {
            Question existingQuestion = getQuestionById(id);
            existingQuestion.setExamType(updatedQuestion.getExamType());
            existingQuestion.setSubject(updatedQuestion.getSubject());
            existingQuestion.setDepartment(updatedQuestion.getDepartment());
            existingQuestion.setBatch(updatedQuestion.getBatch());
            existingQuestion.setSemester(updatedQuestion.getSemester());
            existingQuestion.setPart(updatedQuestion.getPart());
            existingQuestion.setQuestionNumber(updatedQuestion.getQuestionNumber());
            existingQuestion.setText(updatedQuestion.getText());
            existingQuestion.setMaxMarks(updatedQuestion.getMaxMarks());
            existingQuestion.setCourseOutcome(updatedQuestion.getCourseOutcome());
            return questionRepository.save(existingQuestion);
        } catch (Exception e) {
            throw new RuntimeException("Error updating question: " + e.getMessage());
        }
    }
    
    public List<Question> getQuestionsByDepartment(Long departmentId) {
       try {
    	   
    	   return questionRepository.findByDepartmentId(departmentId);
    	   
       }catch(Exception e) {
    	   throw new RuntimeException("Error getQuestionsByDepartment: " + e.getMessage());
       }
    	
    }
    
    public List<Question> getQuestionsBySubject(String department, String batch, int semester, String examType, String subject) {
    	try {
     	   
    		return questionRepository.findQuestions(department, batch, semester, examType, subject);
     	   
        }catch(Exception e) {
     	   throw new RuntimeException("Error getQuestionsBySubject: " + e.getMessage());
        }
    	
    }
    
  


    @Transactional
    public void saveQuestionsFromExcel(MultipartFile file) throws Exception {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Question> questions = new ArrayList<>();
            List<String> errorMessages = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                try {
                    Question q = new Question();

                    String examType = getCellValue(row.getCell(0));
                    if (examType.isEmpty()) throw new Exception("Exam Type is missing");

                    String deptName = getCellValue(row.getCell(1));
                    String batchName = getCellValue(row.getCell(2));
                    String subjectName = getCellValue(row.getCell(3));
                    String semesterStr = getCellValue(row.getCell(4));
                    String part = getCellValue(row.getCell(5));
                    String questionNo = getCellValue(row.getCell(6));
                    String questionText = getCellValue(row.getCell(7));
                    String maxMarksStr = getCellValue(row.getCell(8));
                    String co = getCellValue(row.getCell(9));

                    if (deptName.isEmpty()) throw new Exception("Department is missing");
                    if (batchName.isEmpty()) throw new Exception("Batch is missing");
                    if (subjectName.isEmpty()) throw new Exception("Subject is missing");
                    if (semesterStr.isEmpty()) throw new Exception("Semester is missing");
                    if (part.isEmpty()) throw new Exception("Part is missing");
                    if (questionNo.isEmpty()) throw new Exception("Question Number is missing");
                    if (questionText.isEmpty()) throw new Exception("Question Text is missing");
                    if (maxMarksStr.isEmpty()) throw new Exception("Max Marks is missing");
                    if (co.isEmpty()) throw new Exception("Course Outcome is missing");

                    int semester = Integer.parseInt(semesterStr);
                    
                    Department dept = departmentService.getDepartmentByName(deptName);
                    Batch batch = batchService.getBatchByName(batchName);
                    Subject subject = subjectService.getSubjectByDepartmentSemesterAndName(dept,semester,subjectName);

                    if (dept == null) throw new Exception("Invalid Department: " + deptName);
                    if (batch == null) throw new Exception("Invalid Batch: " + batchName);
                    if (subject == null) throw new Exception("Invalid Subject: " + subjectName);

                    
                    int maxMarks = Integer.parseInt(maxMarksStr);

                    q.setExamType(ExamTypeNormalizer.normalize(examType));
                    q.setDepartment(dept);
                    q.setBatch(batch);
                    q.setSubject(subject);
                    q.setSemester(semester);
                    q.setPart(part);
                    q.setQuestionNumber(questionNo);
                    q.setText(questionText);
                    q.setMaxMarks(maxMarks);
                    q.setCourseOutcome(co);

                    questions.add(q);
                } catch (Exception e) {
                    errorMessages.add("Row " + (i + 1) + ": " + e.getMessage());
                }
            }

            if (!errorMessages.isEmpty()) {
                throw new RuntimeException("Errors in Excel Upload:\n" + String.join("\n", errorMessages));
            }

            questionRepository.saveAll(questions);
            System.out.println("Successfully uploaded " + questions.size() + " questions.");
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Excel file: " + e.getMessage());
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // format if needed
                }
                return String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", "").trim();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue()).trim();
            case FORMULA:
                try {
                    return cell.getStringCellValue().trim(); // try as string
                } catch (Exception ex) {
                    return String.valueOf(cell.getNumericCellValue()).replaceAll("\\.0$", "").trim();
                }
            case BLANK:
            default:
                return "";
        }
    }



    
    
}

