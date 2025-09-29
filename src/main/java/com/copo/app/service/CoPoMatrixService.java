package com.copo.app.service;

import com.copo.app.model.CoPoMatrixEntry;
import com.copo.app.repository.CoPoMatrixRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CoPoMatrixService {

    private static final Logger logger = LoggerFactory.getLogger(CoPoMatrixService.class);

    @Autowired
    private CoPoMatrixRepository repository;

    public void saveMatrix(String subjectCode, String subjectName, Map<String, String> params) {
        logger.info("Saving CO-PO matrix for subjectCode: {}, subjectName: {}", subjectCode, subjectName);

        List<CoPoMatrixEntry> entries = new ArrayList<>();
        List<String> outcomes = List.of("PO1","PO2","PO3","PO4","PO5","PO6",
                "PO7","PO8","PO9","PO10","PO11","PO12","PSO1","PSO2","PSO3");

        boolean codeExists = !repository.findBySubjectCode(subjectCode).isEmpty();
        boolean nameExists = !repository.findBySubjectName(subjectName).isEmpty();

        if (subjectCode ==null || subjectCode.isEmpty() || subjectName ==null || subjectName.isEmpty()) {
            logger.warn("Subject Code or Subject Name is empty or null subjectCode={}, subjectName={}", codeExists, nameExists);
            throw new IllegalArgumentException("Subject Code or Subject Name is empty or null");
        }
        for (int co = 1; co <= 5; co++) {
            for (String outcome : outcomes) {
                String key = String.format("matrixEntries[%d][%s]", co, outcome);
                if (params.containsKey(key)) {
                    String val = params.get(key).trim();
                    if (!val.isEmpty()) {
                        CoPoMatrixEntry entry = new CoPoMatrixEntry();
                        entry.setSubjectCode(subjectCode);
                        entry.setSubjectName(subjectName);
                        entry.setCoNumber(co);
                        entry.setOutcome(outcome);
                        entry.setLevel((val.isEmpty() || val.equals("-")) ? null : (int) Double.parseDouble(val));
                        entries.add(entry);
                    }
                }
            }
        }

        logger.debug("Total entries prepared: {}", entries.size());
        repository.saveAll(entries);
        logger.info("Entries saved successfully.");
    }

    public List<CoPoMatrixEntry> getMatrixBySubjectCode(String subjectCode) {
        return repository.findBySubjectCode(subjectCode);
    }

    public void parseAndSaveExcel(MultipartFile file, String subjectCode, String subjectName) throws Exception {
        logger.info("Parsing Excel for subjectCode: {}, subjectName: {}", subjectCode, subjectName);

        // Check for existing entries by subject code or subject name
        boolean codeExists = !repository.findBySubjectCode(subjectCode).isEmpty();
        boolean nameExists = !repository.findBySubjectName(subjectName).isEmpty();

        if (codeExists || nameExists) {
            logger.warn("Duplicate subject detected: subjectCodeExists={}, subjectNameExists={}", codeExists, nameExists);
            throw new IllegalArgumentException("Subject Code or Subject Name already exists.");
        }

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            List<CoPoMatrixEntry> entries = new ArrayList<>();

            Row headerRow = sheet.getRow(0);
            List<String> outcomes = new ArrayList<>();
            for (int j = 1; j < headerRow.getLastCellNum(); j++) {
                outcomes.add(headerRow.getCell(j).getStringCellValue().trim());
            }

            for (int i = 1; i <= 5; i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                int coNumber = (int) row.getCell(0).getNumericCellValue();

                for (int j = 1; j <= outcomes.size(); j++) {
                    Cell cell = row.getCell(j);
                    String value = (cell == null) ? "-" : cell.toString().trim();

                    CoPoMatrixEntry entry = CoPoMatrixEntry.builder()
                            .subjectCode(subjectCode)
                            .subjectName(subjectName)
                            .coNumber(coNumber)
                            .outcome(outcomes.get(j - 1))
                            .level((value.isEmpty() || value.equals("-")) ? null : (int) Double.parseDouble(value))
                            .build();

                    entries.add(entry);
                }

            }

            logger.debug("Parsed {} entries from Excel", entries.size());
            repository.saveAll(entries);
            logger.info("CO-PO matrix saved successfully from Excel.");
        } catch (Exception e) {
            logger.error("Failed to parse and save Excel file", e);
            throw e;
        }
    }
    
    
    
    
    /**
     * Load matrix entries for a subject and convert to JSON-friendly Map structure:
     * {
     *   "outcomes": ["PO1", "PO2", ..., "PSO2"],
     *   "matrix": {
     *      "1": {"PO1": 2, "PO2": 1, ...},
     *      ...
     *      "5": {...}
     *    },
     *   "weightedAverage": { ... } // optional, compute if needed
     * }
     */
    public Map<String, Object> getMatrixData(String subjectName, String subjectCode) {
        List<CoPoMatrixEntry> entries = repository.findBySubjectNameAndSubjectCode(subjectName, subjectCode);

        // Extract distinct outcomes sorted (PO1...PO12, PSO1, PSO2)
        Set<String> outcomeSet = new TreeSet<>(Comparator.comparing(String::toString));
        outcomeSet.addAll(entries.stream()
            .map(CoPoMatrixEntry::getOutcome)
            .collect(Collectors.toSet()));

        // Sort outcomes explicitly: PO1 to PO12, then PSO1, PSO2
        List<String> outcomes = new ArrayList<>();
        for (int i = 1; i <= 12; i++) outcomes.add("PO" + i);
        outcomes.add("PSO1");
        outcomes.add("PSO2");
        outcomes.add("PSO3");

        // Filter only outcomes present in data to avoid empty columns
        outcomes = outcomes.stream().filter(outcomeSet::contains).collect(Collectors.toList());

        // Create matrix map: CO number -> (Outcome -> Level or "-")
        Map<Integer, Map<String, String>> matrix = new HashMap<>();
        for (int co = 1; co <= 5; co++) {
            Map<String, String> row = new LinkedHashMap<>();
            for (String outcome : outcomes) {
                row.put(outcome, "-");
            }
            matrix.put(co, row);
        }

        // Fill matrix with values from DB entries
        for (CoPoMatrixEntry e : entries) {
            int coNum = e.getCoNumber();
            String outcome = e.getOutcome();
            Integer level = e.getLevel();

            if (matrix.containsKey(coNum) && matrix.get(coNum).containsKey(outcome)) {
                matrix.get(coNum).put(outcome, level != null ? level.toString() : "-");
            }
        }

        // (Optional) Compute weighted average per outcome
        Map<String, String> weightedAverage = computeWeightedAverage(matrix);

        Map<String, Object> result = new HashMap<>();
        result.put("outcomes", outcomes);
        result.put("matrix", matrix);
        result.put("weightedAverage", weightedAverage);

        return result;
    }

    private Map<String, String> computeWeightedAverage(Map<Integer, Map<String, String>> matrix) {
        Map<String, Double> sumMap = new HashMap<>();
        Map<String, Integer> countMap = new HashMap<>();

        for (Map<String, String> row : matrix.values()) {
            for (Map.Entry<String, String> e : row.entrySet()) {
                String outcome = e.getKey();
                String valStr = e.getValue();
                if (!"-".equals(valStr)) {
                    try {
                        int val = Integer.parseInt(valStr);
                        sumMap.put(outcome, sumMap.getOrDefault(outcome, 0.0) + val);
                        countMap.put(outcome, countMap.getOrDefault(outcome, 0) + 1);
                    } catch (NumberFormatException ex) {
                        // Ignore invalid numbers
                    }
                }
            }
        }

        Map<String, String> weightedAvg = new HashMap<>();
        for (String outcome : sumMap.keySet()) {
            double avg = sumMap.get(outcome) / countMap.get(outcome);
            weightedAvg.put(outcome, String.format("%.2f", avg));
        }

        // Fill missing outcomes with "-"
        for (String outcome : matrix.get(1).keySet()) {
            weightedAvg.putIfAbsent(outcome, "-");
        }

        return weightedAvg;
    }
}
