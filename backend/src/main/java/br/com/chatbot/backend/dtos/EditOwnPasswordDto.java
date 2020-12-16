package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditOwnPasswordDto {
    private int userId;
    private String oldPassword;
    private String newPassword;
    private String newPasswordConfirmation;
}
