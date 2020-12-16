package br.com.chatbot.backend.utils;

import br.com.chatbot.backend.dtos.AuthenticationDto;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuthenticated {
    private UserAuthenticated() {}

    public static AuthenticationDto getUserAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (AuthenticationDto) authentication.getPrincipal();
    }
}
