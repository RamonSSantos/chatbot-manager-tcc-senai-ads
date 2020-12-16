package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.KeywordDto;
import br.com.chatbot.backend.models.KeywordEntity;
import java.util.List;
import java.util.Optional;

public interface KeywordService {
    List<KeywordEntity> findAll();

    Optional<KeywordEntity> findById(Integer id);

    KeywordEntity findByDescription(String description);

    KeywordDto save(KeywordDto keywordDto);

    KeywordDto edit(KeywordDto keywordDto);
}
