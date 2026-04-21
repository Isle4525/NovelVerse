package com.novelverse.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Async
    public void sendVerificationEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("Код подтверждения — NovelVerse");

            helper.setText("""
                <div style="font-family:sans-serif;max-width:480px;margin:0 auto;padding:32px;background:#1a1a2e;color:#e0dfe4;border-radius:16px;">
                    <h2 style="color:#ff3e6c;">NovelVerse</h2>
                    <p>Привет! Твой код подтверждения:</p>
                    <div style="margin:24px 0;padding:20px;background:rgba(255,62,108,0.1);border:1px solid rgba(255,62,108,0.3);border-radius:12px;text-align:center;">
                        <span style="font-size:36px;font-weight:bold;letter-spacing:12px;color:#ff3e6c;">%s</span>
                    </div>
                    <p style="font-size:13px;color:rgba(255,255,255,0.4);">Код действителен 15 минут.</p>
                    <p style="font-size:12px;color:rgba(255,255,255,0.3);">Если вы не регистрировались — просто проигнорируйте это письмо.</p>
                </div>
            """.formatted(code), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка отправки письма: " + e.getMessage());
        }
    }
}
