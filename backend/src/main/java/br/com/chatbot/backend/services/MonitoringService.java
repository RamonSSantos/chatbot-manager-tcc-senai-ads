package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.dtos.LogDto;
import br.com.chatbot.backend.models.MonitoringEntity;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MonitoringService {
    Page<MonitoringEntity> getAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc);

    List<MonitoringEntity> getByContentTitle(String title);

    LogDto createLogByIdAndStatus(int id, int action);

    void editStatus(int id, int status);

    List<BasicReportDto> getAllStatusAndCountIdGroupByStatus();
}
