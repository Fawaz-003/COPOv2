package com.copo.app.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.copo.app.model.Question;
import com.copo.app.model.Student;
import com.copo.app.model.StudentMarks;
import com.copo.app.repository.FacultyMarksViewProjection;
import com.copo.app.repository.FacultyMarksViewRepo;
import com.copo.app.repository.QuestionRepository;
import com.copo.app.repository.StudentMarksRepository;
import com.copo.app.repository.StudentRepository;

@Service
public class FacultyMarksViewService {
	@Autowired
	private FacultyMarksViewRepo facultyMarksViewRepo;

	@Autowired
	private QuestionRepository questionRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private StudentMarksRepository marksRepository;

    public Map<String, Object> getFilteredMarks(Long departmentId, Long batchId, Integer semester, String examType,
            Long subjectId, Integer sectionId) {
        List<FacultyMarksViewProjection> rawData = (sectionId != null)
                ? facultyMarksViewRepo.getFilteredMarksBySection(departmentId, batchId, semester, examType, subjectId, sectionId)
                : facultyMarksViewRepo.getFilteredMarks(departmentId, batchId, semester, examType, subjectId);

		//System.out.println("Service Layer rawData  --> " + rawData);

		// Extract unique questions
		List<Map<String, Object>> questions = rawData.stream().map(p -> {
			Map<String, Object> questionMap = new LinkedHashMap<>();
			questionMap.put("part", p.getPart());
			questionMap.put("questionNumber", p.getQuestionNumber());
			questionMap.put("text", p.getQuestionText());
			questionMap.put("maxMarks", p.getMaxMarks());
			questionMap.put("courseOutcome", p.getCourseOutcome());
			questionMap.put("maxMarks", p.getMaxMarks());
			return questionMap;
		}).distinct().collect(Collectors.toList());

		// Group data by student ID
		List<Map<String, Object>> students = rawData.stream()
				.collect(Collectors.groupingBy(FacultyMarksViewProjection::getStudentId)).entrySet().stream()
				.map(entry -> {
					Map<String, Object> studentData = new LinkedHashMap<>();
					// Get the student's basic info (assuming it's the same for all their records)
					studentData.put("name", entry.getValue().get(0).getStudentName());

					// Aggregate marks for this student
					Map<String, String> marksMap = new LinkedHashMap<>();
					entry.getValue().forEach(p -> {
						marksMap.put("Part " + p.getPart() + " - Q" + p.getQuestionNumber(),
								p.getSubmittedMarks() == null ? "" : p.getSubmittedMarks());
					});

					studentData.put("marks", marksMap);
					return studentData;
				}).collect(Collectors.toList());

		// Prepare the final response
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("questions", questions);
		response.put("students", students);

		return response;
	}
	
	

	public Map<String, Object> getFullGroupedMarksWithoutExamType(Long departmentId, Long batchId, Integer semester,
			Long subjectId) {
		List<FacultyMarksViewProjection> rawData = facultyMarksViewRepo.getFullMarksWithoutExamType(departmentId,
				batchId, semester, subjectId);
		
		//System.out.println("First ==> "+rawData);

		// Group Questions by CO → Exam Type → Questions
		Map<String, Map<String, Map<String, List<Map<String, Object>>>>> groupedQuestions = groupQuestionsByCOExamType(
				rawData);

		//System.out.println("Second ==> "+groupedQuestions);
		
		// Group Student Marks
		Map<Long, Map<String, Object>> studentMap = groupStudentMarks(rawData);
		
		//System.out.println("Third ==> "+studentMap);

		// Calculate >=50% Count and Attempted for each question
		Map<String, Map<String, Map<String, Map<String, Integer>>>> questionStats = calculateQuestionStats(groupedQuestions, studentMap);

		//Map<String, Map<String, Map<String, Integer>>> questionStats = calculateQuestionStats(groupedQuestions,studentMap);

		// Calculate assessment-wise percentage per CO
		Map<String, Map<String, Double>> assessmentPercentages = calculateAssessmentPercentages(questionStats);
		
		//questionswise percentage %
		Map<String, Map<String, Map<String, Double>>> questionWisePercentages =  calculateQuestionWisePercentages(questionStats);
		
		// overall percentage %
		Map<String, Double> averageQuestionWisePercentages =calculateAverageQuestionWisePercentages(questionWisePercentages);



		// Prepare the final response
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("groupedQuestions", groupedQuestions);
		response.put("students", studentMap.values());
		response.put("questionStats", questionStats); // Add the question statistics (>=50% and attempted count)
		response.put("assessmentPercentages", assessmentPercentages);
		response.put("questionWisePercentages", questionWisePercentages);
		response.put("averageQuestionWisePercentages", averageQuestionWisePercentages);


		return response;
	}

	// Helper method to group questions by CO and exam type
	private Map<String, Map<String, Map<String, List<Map<String, Object>>>>> groupQuestionsByCOExamType(
			List<FacultyMarksViewProjection> rawData) {
		Map<String, Map<String, Map<String, List<Map<String, Object>>>>> groupedQuestions = new LinkedHashMap<>();

		for (FacultyMarksViewProjection p : rawData) {
			String co = p.getCourseOutcome();
			String examType = p.getExamType();

			Map<String, List<Map<String, Object>>> examTypeMap = groupedQuestions
					.computeIfAbsent(co, k -> new LinkedHashMap<>())
					.computeIfAbsent(examType, k -> new LinkedHashMap<>());

			List<Map<String, Object>> questions = examTypeMap.computeIfAbsent("questions", k -> new ArrayList<>());

			Map<String, Object> question = new LinkedHashMap<>();
			question.put("part", p.getPart());
			question.put("questionNumber", p.getQuestionNumber());
			question.put("text", p.getQuestionText());
			question.put("maxMarks", p.getMaxMarks());
			question.put("questionId", p.getQuestionId());

			if (!questions.contains(question)) {
				questions.add(question);
			}
		}
		return groupedQuestions;
	}

	// Helper method to group student marks by CO, exam type, and question
	private Map<Long, Map<String, Object>> groupStudentMarks(List<FacultyMarksViewProjection> rawData) {
		Map<Long, Map<String, Object>> studentMap = new LinkedHashMap<>();

		for (FacultyMarksViewProjection p : rawData) {
			Long studentId = p.getStudentId();
			String studentName = p.getStudentName();
			String studentRollnumber = p.getRollNumber();

			String co = p.getCourseOutcome();
			String examType = p.getExamType();
			String key = "Part " + p.getPart() + " - Q" + p.getQuestionNumber();

			studentMap.putIfAbsent(studentId, new LinkedHashMap<>());
			studentMap.get(studentId).putIfAbsent("name", studentName);
			studentMap.get(studentId).putIfAbsent("rollNumber", studentRollnumber);

			Map<String, Map<String, Map<String, String>>> coExamMarksMap = (Map<String, Map<String, Map<String, String>>>) studentMap
					.get(studentId).computeIfAbsent("marks", k -> new LinkedHashMap<>());

			coExamMarksMap.computeIfAbsent(co, k -> new LinkedHashMap<>())
					.computeIfAbsent(examType, k -> new LinkedHashMap<>())
					.put(key, p.getSubmittedMarks() == null ? "" : p.getSubmittedMarks());
		}

		return studentMap;
	}

	private Map<String, Map<String, Map<String, Map<String, Integer>>>> calculateQuestionStats(
	        Map<String, Map<String, Map<String, List<Map<String, Object>>>>> groupedQuestions,
	        Map<Long, Map<String, Object>> studentMap) {

	    Map<String, Map<String, Map<String, Map<String, Integer>>>> questionStats = new LinkedHashMap<>();

	    for (String co : groupedQuestions.keySet()) {
	        Map<String, Map<String, List<Map<String, Object>>>> examTypeMap = groupedQuestions.get(co);

	        for (String examType : examTypeMap.keySet()) {
	            List<Map<String, Object>> questions = examTypeMap.get(examType).get("questions");

	            for (Map<String, Object> question : questions) {
	                String key = "Part " + question.get("part") + " - Q" + question.get("questionNumber");

	                // Init nested maps
	                questionStats.putIfAbsent(co, new LinkedHashMap<>());
	                questionStats.get(co).putIfAbsent(examType, new LinkedHashMap<>());

	                int attemptedCount = calculateAttemptedCount(co, examType, question, studentMap);
	                int above50Count = calculateAbove50Count(co, examType, question, studentMap);

	                Map<String, Integer> stats = new LinkedHashMap<>();
	                stats.put("attempted", attemptedCount);
	                stats.put(">=50%", above50Count);

	                questionStats.get(co).get(examType).put(key, stats);
	            }
	        }
	    }

	    return questionStats;
	}


	private int calculateAbove50Count(String co, String examType, Map<String, Object> question,
			Map<Long, Map<String, Object>> studentMap) {
		int count = 0;
		int maxMarks = (Integer) question.get("maxMarks");
		int threshold = (int) Math.ceil(maxMarks * 0.5); // Correct rounding
		String key = "Part " + question.get("part") + " - Q" + question.get("questionNumber");

		//System.out.println("inside CalculateAbove50Count");
		//System.out.println("CO =>" +co+" examType => "+examType+" question => "+question+" studentMap => "+studentMap);
		//System.out.println("Max marks ==> " +maxMarks);
		//System.out.println("threshold ==> "+threshold);
		//System.out.println("key ==> " +key);

		for (Long studentId : studentMap.keySet()) {
			Map<String, Map<String, Map<String, String>>> marksMap = (Map<String, Map<String, Map<String, String>>>) studentMap
					.get(studentId).get("marks");
			
			//System.out.println("studentId ==> "+studentId);
			//System.out.println("key ==> " +key);
			//System.out.println("marksMap ==>" +marksMap);

			if (marksMap.containsKey(co) && marksMap.get(co).containsKey(examType)
					&& marksMap.get(co).get(examType).containsKey(key)) {

				
				
				String mark = marksMap.get(co).get(examType).get(key);
				
				//System.out.println("studentId ==> "+studentId);
				//System.out.println("key ==> " +key);
				//System.out.println("first if loop  ==> "+mark);
				
				if (mark != null && !mark.isEmpty()) {
					//System.out.println("second loop");
					try {
						Double numericMark = Double.parseDouble(mark);
						if (numericMark >= threshold) {
							
							count++;
							
							//System.out.println("final count ==> "+count);
						}
					} catch (NumberFormatException e) {
// ignore non-numeric values
					}
				}
			}
		}

		return count;
	}

	private int calculateAttemptedCount(String co, String examType, Map<String, Object> question,
			Map<Long, Map<String, Object>> studentMap) {
		int count = 0;
		String key = "Part " + question.get("part") + " - Q" + question.get("questionNumber");

		for (Long studentId : studentMap.keySet()) {
			Map<String, Map<String, Map<String, String>>> marksMap = (Map<String, Map<String, Map<String, String>>>) studentMap
					.get(studentId).get("marks");

			if (marksMap.containsKey(co) && marksMap.get(co).containsKey(examType)
					&& marksMap.get(co).get(examType).containsKey(key)) {

				String mark = marksMap.get(co).get(examType).get(key);
				if (mark != null && !mark.isEmpty() && !mark.contains("N") && !mark.contains("AB")) {
					count++;
				}
			}
		}

		return count;
	}

	
	
	// Assessment wise percentage
	private Map<String, Map<String, Double>> calculateAssessmentPercentages(
	        Map<String, Map<String, Map<String, Map<String, Integer>>>> questionStats) {

	    Map<String, Map<String, Double>> assessmentPercentages = new LinkedHashMap<>();

	    for (String co : questionStats.keySet()) {
	        Map<String, Double> examPercentMap = new LinkedHashMap<>();
	        Map<String, Map<String, Map<String, Integer>>> examTypeStats = questionStats.get(co);

	        for (String examType : examTypeStats.keySet()) {
	            int totalAbove50 = 0;
	            int totalAttempted = 0;

	            for (Map<String, Integer> questionStat : examTypeStats.get(examType).values()) {
	                totalAbove50 += questionStat.getOrDefault(">=50%", 0);
	                totalAttempted += questionStat.getOrDefault("attempted", 0);
	            }

	            double percentage = (totalAttempted == 0) ? 0.0 : ((double) totalAbove50 / totalAttempted) * 100.0;
	            examPercentMap.put(examType, percentage);
	        }

	        assessmentPercentages.put(co, examPercentMap);
	    }

	    return assessmentPercentages;
	}
	
	
	//questions wise percentage %
	private Map<String, Map<String, Map<String, Double>>> calculateQuestionWisePercentages(
	        Map<String, Map<String, Map<String, Map<String, Integer>>>> questionStats) {

	    Map<String, Map<String, Map<String, Double>>> questionWisePercentages = new LinkedHashMap<>();

	    for (String co : questionStats.keySet()) {
	        Map<String, Map<String, Double>> examTypeMap = new LinkedHashMap<>();
	        Map<String, Map<String, Map<String, Integer>>> examTypes = questionStats.get(co);

	        Map<String, Map<String, Double>> examMap = new LinkedHashMap<>();

	        for (String examType : examTypes.keySet()) {
	            Map<String, Double> questionMap = new LinkedHashMap<>();

	            for (String questionKey : examTypes.get(examType).keySet()) {
	                Map<String, Integer> stats = examTypes.get(examType).get(questionKey);
	                int attempted = stats.getOrDefault("attempted", 0);
	                int above50 = stats.getOrDefault(">=50%", 0);

	                double percentage = (attempted == 0) ? 0.0 : ((double) above50 / attempted) * 100.0;
	                questionMap.put(questionKey, percentage);
	            }

	            examMap.put(examType, questionMap);
	        }

	        questionWisePercentages.put(co, examMap);
	    }

	    return questionWisePercentages;
	}

	
	
	//overall percentage CO wise
	private Map<String, Double> calculateAverageQuestionWisePercentages(
	        Map<String, Map<String, Map<String, Double>>> questionWisePercentages) {

	    Map<String, Double> averagePercentages = new LinkedHashMap<>();

	    for (String co : questionWisePercentages.keySet()) {
	        Map<String, Map<String, Double>> examTypeMap = questionWisePercentages.get(co);

	        double sum = 0.0;
	        int count = 0;

	        for (Map<String, Double> questionMap : examTypeMap.values()) {
	            for (double percentage : questionMap.values()) {
	                sum += percentage;
	                count++;
	            }
	        }

	        double average = (count == 0) ? 0.0 : sum / count;
	        averagePercentages.put(co, average);
	    }

	    return averagePercentages;
	}



}
