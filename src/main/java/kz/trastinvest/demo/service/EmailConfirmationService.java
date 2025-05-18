package kz.trastinvest.demo.service;

import kz.trastinvest.demo.model.EmailConfirmation;
import kz.trastinvest.demo.repositories.EmailConfirmationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class EmailConfirmationService {

    @Autowired
    private EmailConfirmationRepository repository;

    @Autowired
    private EmailService emailService;

    public void sendConfirmation(String email) {
        String code = String.valueOf(new Random().nextInt(899999) + 100000);
        EmailConfirmation confirmation = new EmailConfirmation();
        confirmation.setEmail(email);
        confirmation.setCode(code);
        confirmation.setConfirmed(false);
        confirmation.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        repository.save(confirmation);

        emailService.sendSimpleMail("Confirmation Code", "Your code is: " + code);
    }

    public boolean confirmCode(String email, String code) {
        Optional<EmailConfirmation> optional = repository.findTopByEmailOrderByExpiresAtDesc(email);
        if (optional.isEmpty()) return false;
        EmailConfirmation conf = optional.get();
        if (!conf.getCode().equals(code) || conf.getExpiresAt().isBefore(LocalDateTime.now())) return false;

        conf.setConfirmed(true);
        repository.save(conf);
        return true;
    }

    public boolean isConfirmed(String email) {
        return repository.findTopByEmailOrderByExpiresAtDesc(email)
                .map(EmailConfirmation::isConfirmed)
                .orElse(false);
    }
}
