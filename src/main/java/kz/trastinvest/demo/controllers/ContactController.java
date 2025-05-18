package kz.trastinvest.demo.controllers;

import kz.trastinvest.demo.dto.request.ContactRequest;
import kz.trastinvest.demo.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/send-message")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> send(@RequestBody ContactRequest request) {
        emailService.sendContactForm(request);
        return ResponseEntity.ok("Message sent successfully");
    }
}
