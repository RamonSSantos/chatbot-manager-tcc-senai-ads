package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.SectorDto;
import br.com.chatbot.backend.models.SectorEntity;
import br.com.chatbot.backend.services.SectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/sector", method = RequestMethod.GET)
@RestController
public class SectorController {
    private final SectorService sectorService;

    @Autowired
    public SectorController(SectorService sectorService) {
        this.sectorService = sectorService;
    }

    @GetMapping
    public Page<SectorEntity> getAllSectors(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "description") String sortBy,
            @RequestParam(defaultValue = "false") Boolean sortDesc
    ) {
        return this.sectorService.findAll(pageNumber - 1, pageSize, sortBy, sortDesc);
    }

    @GetMapping(value = "/view/{id}")
    public Optional<SectorEntity> getById(@PathVariable int id) {
        return this.sectorService.findById(id);
    }

    @GetMapping(value = "/search")
    public List<SectorEntity> getByDescription(@RequestParam String description) {
        return this.sectorService.findByDescription(description);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody SectorDto sectorDto) {
        this.sectorService.save(sectorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Setor cadastrado com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody SectorDto sectorDto) {
        this.sectorService.edit(sectorDto);
        return ResponseEntity.status(HttpStatus.OK).body("Setor alterado com sucesso!");
    }

    @GetMapping(value = "/get-all-active-sectors")
    public List<SectorEntity> getAllActiveSectors() {
        return sectorService.getBySectorStatus();
    }
}
