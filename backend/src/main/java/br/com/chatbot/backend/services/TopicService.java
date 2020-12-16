package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.TopicDto;
import br.com.chatbot.backend.models.TopicEntity;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface TopicService {
    Page<TopicEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc);

    Optional<TopicEntity> findById(Integer id);

    List<TopicEntity> findByDescription(String description);

    TopicDto save(TopicDto topicDto);

    TopicDto edit(TopicDto topicDto);

    List<TopicEntity> getByTopicStatus();
}
