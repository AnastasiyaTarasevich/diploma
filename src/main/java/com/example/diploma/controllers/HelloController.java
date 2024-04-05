package com.example.diploma.controllers;


import com.example.diploma.models.User;
import com.example.diploma.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class HelloController {
    public static final String CAPTCHA_URL = "https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s";
    @Value("${recaptcha.secret")
    private String secret;
    private final UserService userService;

    private final RestTemplate restTemplate;
    @GetMapping("/")
    public String greeting(Model model)
    {
        model.addAttribute("title", "Добро пожаловать!");
        return "main";
    }
    @GetMapping("/mylogin")
    public String login(Model model, HttpServletRequest request) {
        model.addAttribute("title", "Вход в систему");

        // Получение значения атрибута errorMessage из сессии
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");

        // Проверка, является ли errorMessage сообщением "Пользователь не найден"
        if ("Пользователь не найден".equals(errorMessage)) {
            // Добавление errorMessage в модель представления
            model.addAttribute("errorMessage", errorMessage);

            // Удаление errorMessage из сессии
            request.getSession().removeAttribute("errorMessage");
        }

        return "login";
    }
    @GetMapping("/about_us")
    public String about_us(Model model)
    {
        model.addAttribute("title", "О нас");
        return "about_us";
    }
    @GetMapping("/our_contacts")
    public String our_contacts(Model model)
    {
        model.addAttribute("title", "Контакты");
        return "our_contacts";
    }
    @GetMapping("/for_customer")
    public String for_customer(Model model)
    {
        model.addAttribute("title", "Для пользователя");
        return "for_customer";
    }
    @GetMapping("/for_supplier")
    public String for_supplier(Model model)
    {
        model.addAttribute("title", "Для партнера");
        return "for_supplier";
    }
    @GetMapping("/user/")
    public String getUser(Model model, @AuthenticationPrincipal User user,HttpSession session)
    {
        String username = user.getUsername();

        // Сохранение ника пользователя в сессии
        session.setAttribute("username", username);
        model.addAttribute("username", username);
        model.addAttribute("login",user.getUsername());
        return "user";
    }
    @GetMapping("/admin/")
    public String getAdmin(Model model, @AuthenticationPrincipal User user)
    {
        model.addAttribute("login",user.getUsername());
        model.addAttribute("tittle","Страница администратора");
        return "admin";
    }
    @GetMapping("/supplier/")
    public String getSupplier(Model model, @AuthenticationPrincipal User user, HttpSession session)
    {
        model.addAttribute("login",user.getUsername());
        String username = user.getUsername();

        // Сохранение ника пользователя в сессии
        session.setAttribute("username", username);
        model.addAttribute("username", username);
        model.addAttribute("tittle","Страница поставщика");
        return "supplier";
    }
    @GetMapping("/auth")
    public String auth(Model model)
    {
        model.addAttribute("tittle","Регистрация в системе");
        return "auth";
    }
    @PostMapping("/auth")
    public String addAuhorith(Model model, @Valid User user,
                              @RequestParam(name = "confirmPassword")String confirmPassword,
                              @RequestParam("g-recaptcha-response") String captchaResponse
    )
    {
                String url = String.format(CAPTCHA_URL, secret, captchaResponse);
        System.out.println(captchaResponse);
//        CaptchaResponseDto responses = restTemplate.postForObject(url, Collections.emptyList(), CaptchaResponseDto.class);
        if (captchaResponse==null || captchaResponse.isEmpty()) {
            model.addAttribute("captchaError", "Заполните капчу");
            return "auth";
        }
        if(!confirmPassword.equals(user.getPassword()))
        {
            model.addAttribute("passwError", "Пароли не совпадают");
            return "auth";
        }
        else if(!userService.createUser(user)) {
            model.addAttribute("usernameError", "Пользователь существует!");
            return "auth";
        }
        else return "redirect:/main";

    }
}
