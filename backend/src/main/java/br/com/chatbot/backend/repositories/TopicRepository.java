package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.models.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicRepository extends JpaRepository<TopicEntity, Integer> {
    @Query("select t from TopicEntity t where t.description like :description%")
    List<TopicEntity> getByDescription(@Param("description") String description);

    @Query("select t from TopicEntity t where t.status = 1")
    List<TopicEntity> getByTopicStatus();
}
