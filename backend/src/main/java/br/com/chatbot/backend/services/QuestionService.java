package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.QuestionDto;
import br.com.chatbot.backend.models.QuestionEntity;
import java.util.List;
import java.util.Optional;

public interface QuestionService {
    List<QuestionEntity> findAll();

    Optional<QuestionEntity> findById(Integer id);

    QuestionEntity findByDescription(String description);

    QuestionDto save(QuestionDto questionDto);

    QuestionDto edit(QuestionDto questionDto);
}
