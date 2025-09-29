package com.copo.app.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name ="faculty")
public class Faculty {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	private String facultycode;

    private String name;

    private String designation;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false) // Establishes a relationship with Department
    private Department department;

  
     

}
