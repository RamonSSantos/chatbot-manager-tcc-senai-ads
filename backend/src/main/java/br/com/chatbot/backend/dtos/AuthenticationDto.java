package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationDto {
    private String token;
    private int userId;
    private String userFullname;
    private String userEmail;
    private LocalDateTime startingDate;
    private LocalDateTime endingDate;
}
