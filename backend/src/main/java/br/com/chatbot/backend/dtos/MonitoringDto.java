package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringDto {
    private int id;
    private int status;
    private UserDto user;
    private ContentDto content;
    private List<LogDto> log;
}
