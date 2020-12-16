package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.dtos.BasicReportLogDto;
import br.com.chatbot.backend.models.MonitoringEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MonitoringRepository extends JpaRepository<MonitoringEntity, Integer> {
    @Query("select m from MonitoringEntity m "
            + "inner join ContentEntity c on c.id = m.content.id "
            + "where c.title like :title% order by c.title ")
    List<MonitoringEntity> getByContentTitle(@Param("title") String title);

    @Modifying
    @Query("update from MonitoringEntity m set m.status = :status where m.id = :id")
    void editStatusById(@Param("id") int id, @Param("status") int status);

    @Query("select new br.com.chatbot.backend.dtos.BasicReportLogDto(m.status, count(m.id)) "
            + "from MonitoringEntity m group by m.status ")
    List<BasicReportLogDto> getAllStatusAndCountIdGroupByStatus();
}
