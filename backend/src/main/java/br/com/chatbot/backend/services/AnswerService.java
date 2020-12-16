package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.AnswerDto;
import br.com.chatbot.backend.models.AnswerEntity;
import java.util.List;
import java.util.Optional;

public interface AnswerService {
    List<AnswerEntity> findAll();

    Optional<AnswerEntity> findById(Integer id);

    AnswerEntity findByDescription(String description);

    AnswerDto save(AnswerDto answerDto);

    AnswerDto edit(AnswerDto answerDto);
}
