package com.copo.app.service;

import com.copo.app.model.Batch;
import com.copo.app.repository.BatchRepository;
import com.copo.app.exception.BatchNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {

    private static final Logger logger = LoggerFactory.getLogger(BatchService.class);

    @Autowired
    private BatchRepository batchRepository;

    // Save a new batch
    public Batch saveBatch(Batch batch) {
        try {
            logger.info("Saving new batch: {}", batch.getName());
            return batchRepository.save(batch);
        } catch (Exception e) {
            logger.error("Error while saving batch: {}", batch.getName(), e);
            throw new RuntimeException("Failed to save batch", e);
        }
    }

    // Get all batches
    public List<Batch> getAllBatches() {
        try {
            logger.info("Fetching all batches");
            return batchRepository.findAll();
        } catch (Exception e) {
            logger.error("Error while fetching all batches", e);
            throw new RuntimeException("Failed to fetch batches", e);
        }
    }
    
    public boolean isBatchNameExists(String name) {
    	try {
            logger.info("isBatchNameExists");
            return batchRepository.findBatchByName(name).isPresent();
        } catch (Exception e) {
            logger.error("Error while isBatchNameExists", e);
            throw new RuntimeException("Failed to check isBatchNameExists", e);
        }
        
    }


    // Get a batch by ID
    public Optional<Batch> getBatchById(Long id) {
        try {
            logger.info("Fetching batch by ID: {}", id);
            return Optional.of(batchRepository.findById(id)
                    .orElseThrow(() -> new BatchNotFoundException("Batch not found with id: " + id)));
        } catch (BatchNotFoundException e) {
            logger.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching batch with ID: {}", id, e);
            throw new RuntimeException("Failed to fetch batch with ID: " + id, e);
        }
    }

    // Get a batch by Name
    public Batch getBatchByName(String batchName) {
        try {
            logger.info("Fetching batch by name: {}", batchName);
            return batchRepository.findBatchByName(batchName)
                    .orElseThrow(() -> new RuntimeException("Batch not found: " + batchName));
        } catch (RuntimeException e) {
            logger.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching batch by name: {}", batchName, e);
            throw new RuntimeException("Failed to fetch batch by name: " + batchName, e);
        }
    }

    // Update a batch
    @Transactional
    public Batch updateBatch(Long id, Batch batchDetails) {
        try {
            logger.info("Updating batch with ID: {}", id);
            Batch batch = batchRepository.findById(id)
                    .orElseThrow(() -> new BatchNotFoundException("Batch not found with id: " + id));

            batch.setName(batchDetails.getName());
            batch.setDescription(batchDetails.getDescription());

            return batchRepository.save(batch);
        } catch (BatchNotFoundException e) {
            logger.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating batch with ID: {}", id, e);
            throw new RuntimeException("Failed to update batch with ID: " + id, e);
        }
    }

    // Delete a batch
    @Transactional
    public void deleteBatch(Long id) {
        try {
            logger.info("Deleting batch with ID: {}", id);
            if (!batchRepository.existsById(id)) {
                throw new BatchNotFoundException("Batch not found with ID: " + id);
            }
            batchRepository.deleteById(id);
            logger.info("Batch deleted successfully with ID: {}", id);
        } catch (BatchNotFoundException e) {
            logger.warn(e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error while deleting batch with ID: {}", id, e);
            throw new RuntimeException("Failed to delete batch with ID: " + id, e);
        }
    }
}
