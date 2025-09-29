package com.copo.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.copo.app.model.Batch;


@Repository
public interface BatchRepository extends JpaRepository<Batch, Long> {

	Optional<Batch> findBatchByName(String batch);
    // Add custom query methods if needed
}
