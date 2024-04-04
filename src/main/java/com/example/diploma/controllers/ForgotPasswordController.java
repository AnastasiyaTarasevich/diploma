package com.example.diploma.controllers;


import com.example.diploma.models.User;
import com.example.diploma.models.Utility;
import com.example.diploma.repos.UserRepo;
import com.example.diploma.services.UserService;
import lombok.Data;
import net.bytebuddy.utility.RandomString;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Data
@Controller
public class ForgotPasswordController {

    private final JavaMailSender mailSender;

    private final UserRepo userRepo;
    private final UserService userService;

    @GetMapping("/missingPassword")
    public String getMissingPasswForm(Model model)
    {
        return "missingPassword";
    }

    @PostMapping("/missingPassword")
    public  String processForgotPassword(HttpServletRequest request, @RequestParam(name="login") String login, @RequestParam (name="email") String email, Model model)
    {

        String token = RandomString.make(30);

        try {
            userService.updateResetPasswordToken(token, email);
            String resetPasswordLink = Utility.getSiteURL(request) + "/reset_password?token=" + token;
            sendEmail(email, resetPasswordLink);
            model.addAttribute("message", "Мы отправили вам на почту ссылку для смены пароля. Проверьте ваш почтовый ящик.");

//        } catch (CustomerNotFoundException ex) {
//            model.addAttribute("error", ex.getMessage());
        } catch (UnsupportedEncodingException | MessagingException e) {
            model.addAttribute("error", "Ошибка при отправке сообщения");
        }

        return "missingPassword";
    }

    public void sendEmail(String recipientEmail, String link) throws MessagingException, UnsupportedEncodingException
    {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("nastenatarasevich333@gmail.com", "Anastasiya");

        helper.setTo(recipientEmail);

        String subject = "Тут тебе ссылочка для смены пароля :)";

        String content = "<p>Привет,</p>"
                + "<p>От тебя был отправлен запрос на смену пароля.</p>"
                + "<p>Нажми на ссылку ниже, чтобы его изменить:</p>"
                + "<p><a href=\"" + link + "\">Change my password</a></p>"
                + "<br>"
                + "<p>Проигнорируй сообщение, если помнишь свой пароль "
                + "или не посылал этот запрос.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        mailSender.send(message);

    }


    @GetMapping("/reset_password")
    public String showResetPasswordForm(@Param(value = "token") String token, Model model) {
        User user = userService.getByResetPasswordToken(token);
        model.addAttribute("token", token);

        if (user == null) {
            model.addAttribute("message", "Недействительный токен");
            return "message";
        }

        return "resetPassword_form";
    }

    @PostMapping("/reset_password")
    public String processResetPassword(HttpServletRequest request, Model model) {
        String token = request.getParameter("token");
        String password = request.getParameter("password");

        User customer = userService.getByResetPasswordToken(token);
        model.addAttribute("title", "Сброс пароля");

        if (customer == null) {
            model.addAttribute("message", "Недействительный токен");
            return "message";
        } else
        {
            userService.updatePassword(customer, password);

            model.addAttribute("message", "Вы успешно сменили пароль.");
        }

        return "message";
    }

}
