package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.models.KeywordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface KeywordRepository extends JpaRepository<KeywordEntity, Integer> {
    @Query("select k from KeywordEntity k where k.description like :description%")
    KeywordEntity getByDescription(@Param("description") String description);
}
