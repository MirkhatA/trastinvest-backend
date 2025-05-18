package kz.trastinvest.demo.controllers;

import kz.trastinvest.demo.dto.request.AuthRequest;
import kz.trastinvest.demo.dto.request.ResetPasswordRequest;
import kz.trastinvest.demo.dto.request.ResetRequest;
import kz.trastinvest.demo.model.AdminUser;
import kz.trastinvest.demo.repositories.AdminUserRepository;
import kz.trastinvest.demo.service.EmailConfirmationService;
import kz.trastinvest.demo.service.EmailService;
import kz.trastinvest.demo.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final EmailConfirmationService emailConfirmationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Пользователь с таким email уже существует");
        }

        emailConfirmationService.sendConfirmation(request.getEmail());

        AdminUser user = new AdminUser();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setActive(false); // Добавьте это поле в модель AdminUser
        userRepo.save(user);
        return ResponseEntity.ok("Код подтверждения отправлен на ваш email");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest request) {
        AdminUser user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

        if (!user.isActive()) {
            return ResponseEntity.badRequest().body("Email не подтвержден");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Неверные учетные данные");
        }

        String token = jwtService.generateToken(user.getEmail());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/reset-request")
    public ResponseEntity<String> resetRequest(@RequestBody ResetRequest request) {
        AdminUser user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        user.setResetCode(code);
        userRepo.save(user);

        emailService.sendSimpleMail("Код восстановления", "Ваш код: " + code);
        return ResponseEntity.ok("Reset code sent");
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        AdminUser user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!request.getResetCode().equals(user.getResetCode())) {
            throw new RuntimeException("Invalid code");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setResetCode(null);
        userRepo.save(user);

        return ResponseEntity.ok("Password updated");
    }

    @PostMapping("/register/send-code")
    public ResponseEntity<String> sendCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        emailConfirmationService.sendConfirmation(email);
        return ResponseEntity.ok("Confirmation code sent");
    }

    @PostMapping("/register/confirm-code")
    public ResponseEntity<String> confirmCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean success = emailConfirmationService.confirmCode(email, code);
        if (success) {
            // Активируем пользователя после подтверждения
            AdminUser user = userRepo.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
            user.setActive(true);
            userRepo.save(user);
            return ResponseEntity.ok("Email подтвержден, регистрация завершена");
        }
        return ResponseEntity.badRequest().body("Неверный или просроченный код");
    }
}
