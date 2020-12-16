package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.models.AnswerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AnswerRepository extends JpaRepository<AnswerEntity, Integer> {
    @Query("select a from AnswerEntity a where a.description like :description%")
    AnswerEntity getByDescription(@Param("description") String description);
}
