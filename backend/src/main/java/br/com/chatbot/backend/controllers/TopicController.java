package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.TopicDto;
import br.com.chatbot.backend.models.TopicEntity;
import br.com.chatbot.backend.services.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/topic", method = RequestMethod.GET)
@RestController
public class TopicController {
    private final TopicService topicService;

    @Autowired
    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @GetMapping
    public Page<TopicEntity> getAllTopics(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "description") String sortBy,
            @RequestParam(defaultValue = "false") Boolean sortDesc
    ) {
        return this.topicService.findAll(pageNumber - 1, pageSize, sortBy, sortDesc);
    }

    @GetMapping(value = "/view/{id}")
    public Optional<TopicEntity> getById(@PathVariable int id) {
        return this.topicService.findById(id);
    }

    @GetMapping(value = "/search")
    public List<TopicEntity> getByDescription(@RequestParam String description) {
        return this.topicService.findByDescription(description);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody TopicDto topicDto) {
        this.topicService.save(topicDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Tema cadastrado com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody TopicDto topicDto) {
        this.topicService.edit(topicDto);
        return ResponseEntity.status(HttpStatus.OK).body("Tema alterado com sucesso!");
    }

    @GetMapping(value = "/get-all-active-topics")
    public List<TopicEntity> getAllActiveTopics() {
        return topicService.getByTopicStatus();
    }
}
