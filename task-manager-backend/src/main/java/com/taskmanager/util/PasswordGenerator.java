package com.taskmanager.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = args.length > 0 ? args[0] : "admin";
        String encoded = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + encoded);
        System.out.println("Matches: " + encoder.matches(password, encoded));
    }
}
