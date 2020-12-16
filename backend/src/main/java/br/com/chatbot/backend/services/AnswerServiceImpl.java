package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.AnswerDto;
import br.com.chatbot.backend.models.AnswerEntity;
import br.com.chatbot.backend.repositories.AnswerRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class AnswerServiceImpl implements AnswerService {
    private final AnswerRepository answerRepository;

    @Autowired
    public AnswerServiceImpl(AnswerRepository answerRepository) {
        this.answerRepository = answerRepository;
    }

    @Override
    public List<AnswerEntity> findAll() {
        return this.answerRepository.findAll();
    }

    @Override
    public Optional<AnswerEntity> findById(Integer id) {
        return this.answerRepository.findById(id);
    }

    @Override
    public AnswerEntity findByDescription(String description) {
        return this.answerRepository.getByDescription(description);
    }

    @Override
    public AnswerDto save(AnswerDto answerDto) {
        AnswerEntity answerEntity = new AnswerEntity();
        BeanUtils.copyProperties(answerDto, answerEntity);

        AnswerEntity entity = this.answerRepository.save(answerEntity);
        AnswerDto dto = new AnswerDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public AnswerDto edit(AnswerDto answerDto) {
        AnswerEntity answerEntity = new AnswerEntity();
        BeanUtils.copyProperties(answerDto, answerEntity);

        AnswerEntity entity = this.answerRepository.save(answerEntity);
        AnswerDto dto = new AnswerDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
