package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.dtos.LogMessageDto;
import br.com.chatbot.backend.models.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Integer> {
    @Query("select new br.com.chatbot.backend.dtos.LogMessageDto(l.id, l.date, l.status, l.user.fullname) "
            + "from LogEntity l "
            + "inner join l.message m "
            + "where l.user.id = :userId and m.contentId = :contentId ")
    List<LogMessageDto> getByUserIdAndMessageContentId(@Param("userId") int userId, @Param("contentId") int contentId);
}
