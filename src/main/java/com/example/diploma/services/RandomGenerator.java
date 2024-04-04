package com.example.diploma.services;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class RandomGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int LOGIN_LENGTH = 4;
    private static final int PASSWORD_LENGTH=8;
    private Random random;
    public RandomGenerator()
    {
        random=new Random();
    }
    public String generateLogin() {
        StringBuilder sb = new StringBuilder(LOGIN_LENGTH);

        for (int i = 0; i < LOGIN_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }

    public String generatePassword() {
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(randomIndex);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}
