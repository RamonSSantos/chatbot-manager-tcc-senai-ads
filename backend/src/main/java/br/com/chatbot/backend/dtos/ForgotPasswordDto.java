package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForgotPasswordDto {
    private String cpf;
    private String registration;
    private String email;
    private String password;
    private String passwordConfirmation;
}
