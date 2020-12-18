package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.*;
import br.com.chatbot.backend.enums.MonitoringLogStatusEnum;
import br.com.chatbot.backend.models.LogEntity;
import br.com.chatbot.backend.models.MonitoringEntity;
import br.com.chatbot.backend.models.UserEntity;
import br.com.chatbot.backend.repositories.LogRepository;
import br.com.chatbot.backend.repositories.MonitoringRepository;
import br.com.chatbot.backend.utils.UserAuthenticated;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class MonitoringServiceImpl implements MonitoringService {
    private final MonitoringRepository monitoringRepository;
    private final LogRepository logRepository;
    private final UserService userService;

    @Autowired
    public MonitoringServiceImpl(MonitoringRepository monitoringRepository,
                                 LogRepository logRepository,
                                 UserService userService) {
        this.monitoringRepository = monitoringRepository;
        this.logRepository = logRepository;
        this.userService = userService;
    }

    @Override
    public Page<MonitoringEntity> getAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(!sortDesc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return this.monitoringRepository.findAll(paging);
    }

    @Override
    public List<MonitoringEntity> getByContentTitle(String title) {
        List<MonitoringEntity> monitoringEntity = this.monitoringRepository.getByContentTitle(title);
        if (monitoringEntity.isEmpty()) {
            throw new IllegalArgumentException("Evento não encontrado.");
        }

        return monitoringEntity;
    }

    @Transactional
    @Override
    public LogDto createLogByIdAndStatus(int id, int action) {
        Optional<MonitoringEntity> monitoringEntity = monitoringRepository.findById(id);

        MonitoringEntity entity = monitoringRepository.save(monitoringEntity.orElseThrow(() ->
                new IllegalStateException("Evento não encontrado")));

        saveLog(entity, createLog(Objects.requireNonNull(MonitoringLogStatusEnum.fromId(action)), entity.getUser()));

        return new ModelMapper().map(entity.getLog(), LogDto.class);
    }

    @Transactional
    @Override
    public void editStatus(int id, int status) {
        Optional<MonitoringEntity> monitoringEntity = monitoringRepository.findById(id);
        if (!monitoringEntity.isPresent()) {
            throw new NullPointerException("Evento não encontrado.");
        }

        int monitoringStatus = monitoringEntity.map(MonitoringEntity::getStatus).orElse(null);

        int newStatus;
        if (MonitoringLogStatusEnum.CANCEL.equals(MonitoringLogStatusEnum.fromId(status))) {
            if (!MonitoringLogStatusEnum.SCHEDULE.equals(MonitoringLogStatusEnum.fromId(monitoringStatus))) {
                throw new IllegalStateException("Não é possível cancelar o evento.");
            }

            // Novo status a ser definido
            newStatus = MonitoringLogStatusEnum.CANCEL.getValue();

        } else if (MonitoringLogStatusEnum.RESTART.equals(MonitoringLogStatusEnum.fromId(status))) {
            if (MonitoringLogStatusEnum.SCHEDULE.equals(MonitoringLogStatusEnum.fromId(monitoringStatus))) {
                throw new IllegalStateException("Não é possível reiniciar o fluxo do evento.");
            }

            // Novo status a ser definido
            newStatus = MonitoringLogStatusEnum.SCHEDULE.getValue();

        } else {
            throw new IllegalArgumentException("Status inválido para alteração.");
        }

        monitoringRepository.editStatusById(id, newStatus);

        AuthenticationDto authenticationDto = UserAuthenticated.getUserAuthenticated();
        Optional<UserEntity> userEntity = userService.findById(authenticationDto.getUserId());

        saveLog(monitoringEntity.get(), createLog(Objects.requireNonNull(
                MonitoringLogStatusEnum.fromId(status)), userEntity.orElse(null)));
    }

    @Override
    public List<BasicReportDto> getAllStatusAndCountIdGroupByStatus() {
        List<BasicReportLogDto> basicReportLogDto = monitoringRepository.getAllStatusAndCountIdGroupByStatus(PageRequest.of(0, 5));

        List<BasicReportDto> basicReportDto = new ArrayList<>();

        for (BasicReportLogDto dto: basicReportLogDto) {
            BasicReportDto reportDto = new BasicReportDto();
            reportDto.setAmount(dto.getAmount());
            reportDto.setDescription(Objects.requireNonNull(MonitoringLogStatusEnum.fromId(dto.getStatus())).getDescription());

            basicReportDto.add(reportDto);
        }

        return basicReportDto;
    }

    @Override
    public List<EnumValuesDto> getAllMonitoringLogStatusEnum() {
        List<EnumValuesDto> enumValuesDto = new ArrayList<>();

        List<MonitoringLogStatusEnum> monitoringLogStatusEnum = Arrays.asList(MonitoringLogStatusEnum.values());
        for (MonitoringLogStatusEnum enumValues : monitoringLogStatusEnum) {
            EnumValuesDto dto = new EnumValuesDto();

            dto.setId(enumValues.getValue());
            dto.setDescription(enumValues.getDescription());

            enumValuesDto.add(dto);
        }

        return enumValuesDto;
    }

    @Override
    public List<BasicReportDto> getAllSectorDescriptionAndCountStatusByStatusAndBetweenLogDate(int status,
                                                                                               LocalDate startingDate,
                                                                                               LocalDate endingDate) {
        Objects.requireNonNull(startingDate, "Informe a data inicial.");
        Objects.requireNonNull(endingDate, "Informe a data final.");

        if (startingDate.isAfter(endingDate)) {
            throw new IllegalStateException("A data inicial é maior que a data final.");
        }

        LocalDateTime startDate = startingDate.atStartOfDay();
        LocalDateTime endDate = endingDate.atTime(23, 59, 59);

        List<BasicReportDto> basicReportDto = monitoringRepository.getAllSectorDescriptionAndCountStatusByStatusAndBetweenLogDate(
                status,startDate, endDate, PageRequest.of(0, 10));

        for (BasicReportDto dto : basicReportDto) {
            dto.setDescription(dto.getDescription() == null ? "Não possui setor" : dto.getDescription());
        }

        return basicReportDto;
    }

    private LogEntity createLog(MonitoringLogStatusEnum monitoringLogStatusEnum, UserEntity userEntity) {
        return new LogEntity(LocalDateTime.now(), monitoringLogStatusEnum.getValue(), userEntity);
    }

    private MonitoringEntity saveLog(MonitoringEntity entity, LogEntity logEntity) {
        if (entity.getLog() == null) {
            List<LogEntity> logEntities = new ArrayList<>();
            logEntities.add(logEntity);

            entity.setLog(logEntities);
        } else {
            entity.getLog().add(logEntity);
        }

        entity.getLog().forEach(logRepository::save);

        return entity;
    }
}
