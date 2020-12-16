package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.AnswerDto;
import br.com.chatbot.backend.models.AnswerEntity;
import br.com.chatbot.backend.services.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/answer", method = RequestMethod.GET)
@RestController
public class AnswerController {
    private final AnswerService answerService;

    @Autowired
    public AnswerController(AnswerService answerService) {
        this.answerService = answerService;
    }

    @GetMapping
    public List<AnswerEntity> getAllAnswers() {
        return this.answerService.findAll();
    }

    @GetMapping(value = "/view/{id}")
    public Optional<AnswerEntity> getById(@PathVariable int id) {
        return this.answerService.findById(id);
    }

    @GetMapping(value = "/search")
    public AnswerEntity getByDescription(@RequestParam String description) {
        return this.answerService.findByDescription(description);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody AnswerDto answerDto) {
        this.answerService.save(answerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Resposta cadastrada com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody AnswerDto answerDto) {
        this.answerService.edit(answerDto);
        return ResponseEntity.status(HttpStatus.OK).body("Resposta alterada com sucesso!");
    }
}
