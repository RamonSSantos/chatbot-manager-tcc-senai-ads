package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.models.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionEntity, Integer> {
    @Query("select q from QuestionEntity q where q.description like :description%")
    QuestionEntity getByDescription(@Param("description") String description);
}
