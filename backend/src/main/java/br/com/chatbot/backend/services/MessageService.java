package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.MessageDto;

public interface MessageService {
    MessageDto createLogByIdAndStatusAndUserId(int id, int status, int userId);
}
