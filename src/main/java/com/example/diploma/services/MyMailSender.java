package com.example.diploma.services;

import com.example.diploma.models.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class MyMailSender {
    @Autowired
    private final JavaMailSender javaMailSender;
    public void sendEmailForRegistry(String recipientEmail, String login, String password) throws MessagingException, UnsupportedEncodingException
    {

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom("nastenatarasevich333@gmail.com", "Anastasiya");

        helper.setTo(recipientEmail);

        String subject = "Регистрация в SRM";

        String content = "<p>Вы были зарегистрированы в информационной системе NSRM в качестве поставщика</p>"
                + "<p>Используйте логин и пароль, предоставленный ниже для входа в систему.</p>"
                + "<p>Логин:\""+login+"\"</p>"
                + "<p>Пароль:\""+password+"\"</p>"
                + "<p>Рекомендуем поменять пароль в настройках аккаунта</p>"
                + "<br>"
                + "<p>Проигнорируйте сообщение, если "
                + " не посылали этот запрос.</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);

    }
    public void sendOrderToSupplier(Order order,String recipientEmail) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(order.getUser().getEmail(), order.getUser().getName());
        helper.setTo(recipientEmail);
        String subject = "Поступление нового заказа";

        String content = "<p>Вам поступил новый заказ в системе MySrm</p>"
                + "<p>Заказчик:"+order.getLastName()+" "+order.getFirstName()+"</p>"
                + "<p>Заказ от:"+order.getDate()+"</p>"
        + "<p>Необходимая дата поставки:"+order.getDate_for_sh()+"</p>";

        helper.setSubject(subject);

        helper.setText(content, true);

        javaMailSender.send(message);

    }
}
