package com.radartech.repository;

import com.radartech.domain.Subscription;
import com.radartech.domain.SubscriptionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByCompanyEmailIgnoreCase(String companyEmail);

    Optional<Subscription> findByExternalReference(String externalReference);

    long countByStatus(SubscriptionStatus status);
}
