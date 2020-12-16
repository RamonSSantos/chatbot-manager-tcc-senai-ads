package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.LogMessageDto;
import br.com.chatbot.backend.models.LogEntity;
import java.util.List;

public interface LogService {
    void save(LogEntity logEntity);

    List<LogMessageDto> getByUserIdAndMessageContentId(int userId, int contentId);
}
