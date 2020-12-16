package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.AuthenticationDto;
import br.com.chatbot.backend.dtos.AuthenticationRequestDto;

public interface AuthenticationService {
    AuthenticationDto authenticate(AuthenticationRequestDto requestDto);

    AuthenticationDto validateCookie(String token);

    void logout(String token);
}
