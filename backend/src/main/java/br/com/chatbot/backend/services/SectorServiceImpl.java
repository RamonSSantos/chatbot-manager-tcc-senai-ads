package br.com.chatbot.backend.services;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import br.com.chatbot.backend.dtos.SectorDto;
import br.com.chatbot.backend.models.SectorEntity;
import br.com.chatbot.backend.repositories.SectorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SectorServiceImpl implements SectorService {
    private final SectorRepository sectorRepository;

    @Autowired
    public SectorServiceImpl(SectorRepository sectorRepository) {
        this.sectorRepository = sectorRepository;
    }

    @Override
    public Page<SectorEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(!sortDesc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return this.sectorRepository.findAll(paging);
    }

    @Override
    public Optional<SectorEntity> findById(Integer id) {
        return existsById(id);
    }

    @Override
    public List<SectorEntity> findByDescription(String description) {
        List<SectorEntity> sectorEntity = this.sectorRepository.getByDescription(description);
        if (sectorEntity.isEmpty()) {
            throw new IllegalArgumentException("Setor não encontrado.");
        }

        return sectorEntity;
    }

    @Override
    public SectorDto save(SectorDto sectorDto) {
        Objects.requireNonNull(sectorDto.getDescription(), "Informe a descrição.");

        SectorEntity sectorEntity = new SectorEntity();
        BeanUtils.copyProperties(sectorDto, sectorEntity);

        SectorEntity entity = this.sectorRepository.save(sectorEntity);
        SectorDto dto = new SectorDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public SectorDto edit(SectorDto sectorDto) {
        Objects.requireNonNull(sectorDto.getDescription(), "Informe a descrição.");
        existsById(sectorDto.getId());

        SectorEntity sectorEntity = new SectorEntity();
        BeanUtils.copyProperties(sectorDto, sectorEntity);

        SectorEntity entity = this.sectorRepository.save(sectorEntity);
        SectorDto dto = new SectorDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<SectorEntity> getBySectorStatus() {
        return this.sectorRepository.getBySectorStatus();
    }

    private Optional<SectorEntity> existsById(int id) {
        Optional<SectorEntity> sector = this.sectorRepository.findById(id);
        if (!sector.isPresent()) {
            throw new NullPointerException("Setor não encontrado.");
        }

        return sector;
    }
}
