package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.ForgotPasswordDto;
import br.com.chatbot.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordController {
    private final UserService userService;

    @Autowired
    public PasswordController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        userService.forgotPassword(forgotPasswordDto);

        return ResponseEntity.ok().body("Informe a nova senha.");
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ForgotPasswordDto forgotPasswordDto) {
        userService.resetPassword(forgotPasswordDto);

        return ResponseEntity.ok().body("Senha alterada com sucesso.");
    }
}
