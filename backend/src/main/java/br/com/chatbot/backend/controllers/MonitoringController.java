package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.models.MonitoringEntity;
import br.com.chatbot.backend.services.MonitoringService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequestMapping(value = "/api/monitoring", method = RequestMethod.GET)
@RestController
public class MonitoringController {
    private final MonitoringService monitoringService;

    @Autowired
    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping
    public Page<MonitoringEntity> getAll(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") Boolean sortDesc
    ) {
        return this.monitoringService.getAll(pageNumber - 1, pageSize, sortBy, sortDesc);
    }

    @GetMapping(value = "/search")
    public List<MonitoringEntity> getByContentTitle(@RequestParam String title) {
        return this.monitoringService.getByContentTitle(title);
    }

    @PostMapping(value = "/create-log/id/{id}/status/{status}")
    public ResponseEntity<String> createLog(@PathVariable int id, @PathVariable int status) {
        monitoringService.createLogByIdAndStatus(id, status);
        return ResponseEntity.status(HttpStatus.CREATED).body("Log criado com sucesso!");
    }

    @PutMapping(value = "/edit-status/id/{id}/status/{status}")
    public ResponseEntity<String> editStatus(@PathVariable int id, @PathVariable int status) {
        monitoringService.editStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).body("Evento alterado com sucesso!");
    }

    @GetMapping(value = "/report/get-all-group-by-status")
    public ResponseEntity<List<BasicReportDto>> getAllGroupByStatus() {
        return ResponseEntity.status(HttpStatus.OK).body(monitoringService.getAllStatusAndCountIdGroupByStatus());
    }
}
