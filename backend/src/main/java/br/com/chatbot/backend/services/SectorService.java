package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.SectorDto;
import br.com.chatbot.backend.models.SectorEntity;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface SectorService {
    Page<SectorEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc);

    Optional<SectorEntity>findById(Integer id);

    List<SectorEntity> findByDescription(String description);

    SectorDto save(SectorDto sectorDto);

    SectorDto edit(SectorDto sectorDto);

    List<SectorEntity> getBySectorStatus();
}
