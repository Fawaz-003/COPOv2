package com.copo.app.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"department"})
@Table(name="subjects")
public class Subject {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    @ManyToOne
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    private int semester; // Semester to which the subject belongs

}
