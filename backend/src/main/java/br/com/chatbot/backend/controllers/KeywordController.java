package br.com.chatbot.backend.controllers;

import java.util.List;
import java.util.Optional;
import br.com.chatbot.backend.dtos.KeywordDto;
import br.com.chatbot.backend.models.KeywordEntity;
import br.com.chatbot.backend.services.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/keyword", method = RequestMethod.GET)
@RestController
public class KeywordController {
    private final KeywordService keywordService;

    @Autowired
    public KeywordController(KeywordService keywordService) {
        this.keywordService = keywordService;
    }

    @GetMapping
    public List<KeywordEntity> getAllKeywords() {
        return this.keywordService.findAll();
    }

    @GetMapping(value = "/view/{id}")
    public Optional<KeywordEntity> getById(@PathVariable int id) {
        return this.keywordService.findById(id);
    }

    @GetMapping(value = "/search")
    public KeywordEntity getByDescription(@RequestParam String description) {
        return this.keywordService.findByDescription(description);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody KeywordDto keywordDto) {
        this.keywordService.save(keywordDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Palavra-chave cadastrada com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody KeywordDto keywordDto) {
        this.keywordService.edit(keywordDto);
        return ResponseEntity.status(HttpStatus.OK).body("Palavra-chave alterada com sucesso!");
    }
}
