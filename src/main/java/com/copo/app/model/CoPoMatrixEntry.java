package com.copo.app.model;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "CoPoMatrixEntry")
public class CoPoMatrixEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectCode;
    private String subjectName;

    private int coNumber; // CO1 to CO5
    private String outcome; // PO1..PO12, PSO1, PSO2
    private Integer level; // 1, 2, 3 or null for '-'

}