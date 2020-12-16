package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.LogMessageDto;
import br.com.chatbot.backend.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping(value = "/api/log", method = RequestMethod.GET)
@RestController
public class LogController {
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping(value = "/user-id/{userId}/message-content-id/{contentId}")
    public List<LogMessageDto> getByUserIdAndMessageContentId(@PathVariable int userId, @PathVariable int contentId) {
        return logService.getByUserIdAndMessageContentId(userId, contentId);
    }
}
