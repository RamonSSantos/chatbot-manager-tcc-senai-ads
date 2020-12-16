package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.LogMessageDto;
import br.com.chatbot.backend.models.LogEntity;
import br.com.chatbot.backend.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LogServiceImpl implements LogService {
    private final LogRepository logRepository;

    @Autowired
    public LogServiceImpl(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public void save(LogEntity logEntity) {
        logRepository.save(logEntity);
    }

    @Override
    public List<LogMessageDto> getByUserIdAndMessageContentId(int userId, int contentId) {
        return logRepository.getByUserIdAndMessageContentId(userId, contentId);
    }
}
