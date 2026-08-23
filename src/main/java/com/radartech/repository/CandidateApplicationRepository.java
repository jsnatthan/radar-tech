package com.radartech.repository;

import com.radartech.domain.CandidateApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CandidateApplicationRepository extends JpaRepository<CandidateApplication, Long> {
    List<CandidateApplication> findAllByOrderByCreatedAtDesc();
    long countByCreatedAtAfter(Instant createdAt);
}
