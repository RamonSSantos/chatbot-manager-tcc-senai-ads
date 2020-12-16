package br.com.chatbot.backend.services;

import java.util.List;
import java.util.Optional;
import br.com.chatbot.backend.dtos.QuestionDto;
import br.com.chatbot.backend.models.QuestionEntity;
import br.com.chatbot.backend.repositories.QuestionRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    @Override
    public List<QuestionEntity> findAll() {
        return this.questionRepository.findAll();
    }

    @Override
    public Optional<QuestionEntity> findById(Integer id) {
        return this.questionRepository.findById(id);
    }

    @Override
    public QuestionEntity findByDescription(String description) {
        return this.questionRepository.getByDescription(description);
    }

    @Override
    public QuestionDto save(QuestionDto questionDto) {
        QuestionEntity questionEntity = new QuestionEntity();
        BeanUtils.copyProperties(questionDto, questionEntity);

        QuestionEntity entity = this.questionRepository.save(questionEntity);
        QuestionDto dto = new QuestionDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public QuestionDto edit(QuestionDto questionDto) {
        QuestionEntity questionEntity = new QuestionEntity();
        BeanUtils.copyProperties(questionDto, questionEntity);

        QuestionEntity entity = this.questionRepository.save(questionEntity);
        QuestionDto dto = new QuestionDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
