package br.com.chatbot.backend.services;

import java.util.List;
import java.util.Optional;
import br.com.chatbot.backend.dtos.KeywordDto;
import br.com.chatbot.backend.models.KeywordEntity;
import br.com.chatbot.backend.repositories.KeywordRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class KeywordServiceImpl implements KeywordService {
    private final KeywordRepository keywordRepository;

    @Autowired
    public KeywordServiceImpl(KeywordRepository keywordRepository) {
        this.keywordRepository = keywordRepository;
    }

    @Override
    public List<KeywordEntity> findAll() {
        return this.keywordRepository.findAll();
    }

    @Override
    public Optional<KeywordEntity> findById(Integer id) {
        return this.keywordRepository.findById(id);
    }

    @Override
    public KeywordEntity findByDescription(String description) {
        return this.keywordRepository.getByDescription(description);
    }

    @Override
    public KeywordDto save(KeywordDto keywordDto) {
        KeywordEntity keywordEntity = new KeywordEntity();
        BeanUtils.copyProperties(keywordDto, keywordEntity);

        KeywordEntity entity = this.keywordRepository.save(keywordEntity);
        KeywordDto dto = new KeywordDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public KeywordDto edit(KeywordDto keywordDto) {
        KeywordEntity keywordEntity = new KeywordEntity();
        BeanUtils.copyProperties(keywordDto, keywordEntity);

        KeywordEntity entity = this.keywordRepository.save(keywordEntity);
        KeywordDto dto = new KeywordDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
