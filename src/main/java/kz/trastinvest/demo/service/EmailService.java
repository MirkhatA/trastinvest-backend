package kz.trastinvest.demo.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kz.trastinvest.demo.dto.request.ContactRequest;
import kz.trastinvest.demo.dto.request.ProductSelectionRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${mail.to}")
    private String to;

    public void sendContactForm(ContactRequest request) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject("Запрос с сайта");

            StringBuilder body = new StringBuilder();
            body.append("Имя: ").append(request.getName()).append("\n");
            body.append("Телефон: ").append(request.getPhone()).append("\n");
            body.append("Сообщение: ").append(request.getMessage()).append("\n\n");

            if (request.getProducts() != null && !request.getProducts().isEmpty()) {
                body.append("Выбранные товары:\n");
                for (ProductSelectionRequest p : request.getProducts()) {
                    body.append("• ").append(p.getName())
                            .append(" — ").append(p.getQuantity()).append(" шт.\n");
                }
            }

            helper.setText(body.toString());

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке письма", e);
        }
    }

    public void sendSimpleMail(String subject, String text) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Email error", e);
        }
    }
}
