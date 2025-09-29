package com.copo.app.model;

import java.util.List;

public class QuestionForm {

    private List<Question> questions; // List of Question entities

    // Getters and Setters
    public List<Question> getQuestions() 
    { 
    	return questions;
    	}
    public void setQuestions(List<Question> questions) 
    { 
    	this.questions = questions; 
    	}
}
