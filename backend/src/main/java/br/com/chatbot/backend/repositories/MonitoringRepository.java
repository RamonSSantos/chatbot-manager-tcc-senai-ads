package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.dtos.BasicReportLogDto;
import br.com.chatbot.backend.models.MonitoringEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
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
            + "from MonitoringEntity m group by m.status order by m.status ")
    List<BasicReportLogDto> getAllStatusAndCountIdGroupByStatus(Pageable pageable);

    @Query("select new br.com.chatbot.backend.dtos.BasicReportDto(s.description, count(m.id)) "
            + "from MonitoringEntity m "
            + "inner join ContentEntity c on c.id = m.content.id "
            + "inner join c.log l "
            + "left join SectorEntity s on s.id = c.sector.id "
            + "where m.status = :status and l.date between :startDate and :endDate "
            + "group by s.description order by count(m.id) desc ")
    List<BasicReportDto> getAllSectorDescriptionAndCountStatusByStatusAndBetweenLogDate(@Param("status") int status,
                                                                                        @Param("startDate") LocalDateTime startDate,
                                                                                        @Param("endDate") LocalDateTime endDate,
                                                                                        Pageable pageable);
}
