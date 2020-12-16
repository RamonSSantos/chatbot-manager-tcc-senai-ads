package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.models.SectorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SectorRepository extends JpaRepository<SectorEntity, Integer> {
    @Query("select s from SectorEntity s where s.description like :description%")
    List<SectorEntity> getByDescription(@Param("description") String description);

    @Query("select s from SectorEntity s where s.status = 1")
    List<SectorEntity> getBySectorStatus();
}
