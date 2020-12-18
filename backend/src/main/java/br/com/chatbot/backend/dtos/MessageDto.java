package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private int id;
    private int actor;
    private int status;
    private int userId;
    private int contentId;
    private int questionId;
    private List<LogDto> log;
}
