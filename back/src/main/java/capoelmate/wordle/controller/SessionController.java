package capoelmate.wordle.controller;

import java.security.SecureRandom;
import java.util.Base64;

import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RestController 
@RequestMapping("/api")
public class SessionController {
    
    private static final SecureRandom RNG = new SecureRandom();
    private String generateToken() {
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @GetMapping("/login")
    public String login(@CookieValue(value = "token", required = false) String token, HttpServletResponse response) {
        if (token != null) {
            return "You are already logged in";
        }
        Cookie cookie = new Cookie("token", generateToken());
        cookie.setMaxAge(3600*24); // 24 hours
        cookie.setSecure(false); // despues cambiar a https
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
        return "Login successful!";
    }
    
}
