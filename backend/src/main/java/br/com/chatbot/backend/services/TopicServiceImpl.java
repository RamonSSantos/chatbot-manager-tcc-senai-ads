package br.com.chatbot.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import br.com.chatbot.backend.dtos.TopicDto;
import br.com.chatbot.backend.models.TopicEntity;
import br.com.chatbot.backend.repositories.TopicRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Override
    public Page<TopicEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(!sortDesc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return this.topicRepository.findAll(paging);
    }

    @Override
    public Optional<TopicEntity> findById(Integer id) {
        return existsById(id);
    }

    @Override
    public List<TopicEntity> findByDescription(String description) {
        List<TopicEntity> topicEntity = this.topicRepository.getByDescription(description);
        if (topicEntity.isEmpty()) {
            throw new IllegalArgumentException("Tema não encontrado.");
        }

        return topicEntity;
    }

    @Override
    public TopicDto save(TopicDto topicDto) {
        Objects.requireNonNull(topicDto.getDescription(), "Informe a descrição.");

        TopicEntity topicEntity = new TopicEntity();
        BeanUtils.copyProperties(topicDto, topicEntity);

        TopicEntity entity = this.topicRepository.save(topicEntity);
        TopicDto dto = new TopicDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public TopicDto edit(TopicDto topicDto) {
        Objects.requireNonNull(topicDto.getDescription(), "Informe a descrição.");
        existsById(topicDto.getId());

        TopicEntity topicEntity = new TopicEntity();
        BeanUtils.copyProperties(topicDto, topicEntity);

        TopicEntity entity = this.topicRepository.save(topicEntity);
        TopicDto dto = new TopicDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<TopicEntity> getByTopicStatus() {
        return this.topicRepository.getByTopicStatus();
    }

    private Optional<TopicEntity> existsById(int id) {
        Optional<TopicEntity> topic = this.topicRepository.findById(id);
        if (!topic.isPresent()) {
            throw new NullPointerException("Tema não encontrado.");
        }

        return topic;
    }
}
