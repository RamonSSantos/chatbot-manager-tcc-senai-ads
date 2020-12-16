package br.com.chatbot.backend.controllers;

import java.util.List;
import java.util.Optional;
import br.com.chatbot.backend.dtos.QuestionDto;
import br.com.chatbot.backend.models.QuestionEntity;
import br.com.chatbot.backend.services.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/question", method = RequestMethod.GET)
@RestController
public class QuestionController {
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping
    public List<QuestionEntity> getAllQuestions() {
        return this.questionService.findAll();
    }

    @GetMapping(value = "/view/{id}")
    public Optional<QuestionEntity> getById(@PathVariable int id) {
        return this.questionService.findById(id);
    }

    @GetMapping(value = "/search")
    public QuestionEntity getByDescription(@RequestParam String description) {
        return this.questionService.findByDescription(description);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody QuestionDto questionDto) {
        this.questionService.save(questionDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Pergunta cadastrada com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody QuestionDto questionDto) {
        this.questionService.edit(questionDto);
        return ResponseEntity.status(HttpStatus.OK).body("Pergunta alterada com sucesso!");
    }
}
