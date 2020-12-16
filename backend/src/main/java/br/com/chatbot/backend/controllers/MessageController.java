package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.services.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/message", method = RequestMethod.GET)
@RestController
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping(value = "/create-log/id/{id}/status/{status}/user-id/{userId}")
    public ResponseEntity<String> createLogByIdAndStatusAndUserId(@PathVariable int id, @PathVariable int status, @PathVariable int userId) {
        messageService.createLogByIdAndStatusAndUserId(id, status, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Log criado com sucesso!");
    }
}
