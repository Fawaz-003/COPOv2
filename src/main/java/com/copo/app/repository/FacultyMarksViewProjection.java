package com.copo.app.repository;

public interface FacultyMarksViewProjection {
    Long getStudentId();
    String getStudentName();
    String getRollNumber();
    Long getQuestionId();
    String getPart();
    String getQuestionNumber();
    String getQuestionText();
    Integer getMaxMarks();
    String getExamType();
    String getCourseOutcome(); // New field for course outcome
    String getSubmittedMarks();
}
