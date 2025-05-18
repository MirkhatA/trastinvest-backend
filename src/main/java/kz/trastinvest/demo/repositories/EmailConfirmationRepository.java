package kz.trastinvest.demo.repositories;

import kz.trastinvest.demo.model.EmailConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailConfirmationRepository extends JpaRepository<EmailConfirmation, Long> {
    Optional<EmailConfirmation> findTopByEmailOrderByExpiresAtDesc(String email);
}
