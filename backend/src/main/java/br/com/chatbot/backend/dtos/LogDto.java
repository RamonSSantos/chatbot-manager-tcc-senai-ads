package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogDto {
    private Integer id;
    private LocalDateTime date;
    private int status;
    private UserDto user;
    private List<MessageDto> message;
}
