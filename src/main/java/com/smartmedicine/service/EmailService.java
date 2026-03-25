package com.smartmedicine.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true); // true = HTML

            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendReminderEmail(String to, String userName, String medicineName, String reminderTitle) {
        String subject = "💊 Medicine Reminder: " + medicineName;
        String content = String.format(
            "<html>" +
            "<body style='font-family: Arial, sans-serif; color: #333; line-height: 1.6;'>" +
            "  <div style='max-width: 600px; margin: 0 auto; border: 1px solid #eee; border-radius: 10px; padding: 20px;'>" +
            "    <h2 style='color: #0d9488;'>CareMeds Reminder</h2>" +
            "    <p>Hi <strong>%s</strong>,</p>" +
            "    <p>This is a reminder to take your medicine:</p>" +
            "    <div style='background: #f0fdfa; border-left: 4px solid #0d9488; padding: 15px; margin: 20px 0;'>" +
            "      <div style='font-size: 18px; font-weight: bold;'>%s</div>" +
            "      <div style='color: #666;'>%s</div>" +
            "    </div>" +
            "    <p>Please log in to the app to mark it as taken.</p>" +
            "    <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>" +
            "    <p style='font-size: 12px; color: #999; text-align: center;'>" +
            "      CareMeds — Managing your health with care." +
            "    </p>" +
            "  </div>" +
            "</body>" +
            "</html>",
            userName, medicineName, reminderTitle
        );
        sendEmail(to, subject, content);
    }
}
