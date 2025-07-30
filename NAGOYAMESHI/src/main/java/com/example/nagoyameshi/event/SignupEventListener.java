package com.example.nagoyameshi.event;

import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.VerificationTokenService;

@Component
public class SignupEventListener {

    private final VerificationTokenService verificationTokenService;
    private final JavaMailSender mailSender;

    public SignupEventListener(
        VerificationTokenService verificationTokenService,
        JavaMailSender mailSender
    ) {
        this.verificationTokenService = verificationTokenService;
        this.mailSender = mailSender;
    }

    @EventListener
    public void handleSignupEvent(SignupEvent event) {
        User user = event.getUser();
        String requestUrl = event.getRequestUrl();
        String token = UUID.randomUUID().toString();

        verificationTokenService.create(user, token);

        String verifyUrl = requestUrl + "/signup/verify?token=" + token;

        // メール送信

        sendVerificationEmail(user.getEmail(), verifyUrl);
    }

    private void sendVerificationEmail(String toEmail, String verifyUrl) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("【NAGOYAMESHI】メール認証のご案内");
            helper.setText(
                "以下のリンクをクリックして、メールアドレスの認証を完了してください。\n\n" +
                verifyUrl +
                "\n\nこのメールに心当たりがない場合は、破棄してください。", 
                false
            );

            mailSender.send(message);
        } catch (MessagingException e) {

            e.printStackTrace(); // ログ出力などに切り替えてもOK！
        }
    }
}
