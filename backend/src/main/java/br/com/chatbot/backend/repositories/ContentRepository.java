package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.models.AnswerEntity;
import br.com.chatbot.backend.models.ContentEntity;
import br.com.chatbot.backend.models.KeywordEntity;
import br.com.chatbot.backend.models.QuestionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<ContentEntity, Integer> {
    @Query("select c from ContentEntity c where c.title like :title%")
    List<ContentEntity> getByTitle(@Param("title") String title);

    @Modifying
    @Query("update from ContentEntity c set c.status = :status where c.id = :id")
    void editContentStatus(@Param("id") int id, @Param("status") int status);

    @Query("select c.question from ContentEntity c where c.id = :id")
    List<QuestionEntity> getAllQuestionsByContentId(@Param("id") int id);

    @Query("select c.answer from ContentEntity c where c.id = :id")
    List<AnswerEntity> getAllAnswersByContentId(@Param("id") int id);

    @Query("select c.keyword from ContentEntity c where c.id = :id")
    List<KeywordEntity> getAllKeywordsByContentId(@Param("id") int id);

    @Query("select new br.com.chatbot.backend.dtos.BasicReportDto(t.description, count(c.id)) "
            + "from ContentEntity c "
            + "inner join TopicEntity t on t.id = c.topic.id "
            + "group by t.description ")
    List<BasicReportDto> getAllTopicDescriptionAndCountIdGroupByTopicDescription();

    @Query("select new br.com.chatbot.backend.dtos.BasicReportDto(s.description, count(c.id)) "
            + "from ContentEntity c "
            + "left join SectorEntity s on s.id = c.sector.id "
            + "group by s.description ")
    List<BasicReportDto> getAllSectorDescriptionAndCountIdGroupBySectorDescription();
}
