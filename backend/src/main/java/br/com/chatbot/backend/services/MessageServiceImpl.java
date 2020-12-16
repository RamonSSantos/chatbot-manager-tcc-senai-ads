package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.MessageDto;
import br.com.chatbot.backend.enums.MessageLogStatusEnum;
import br.com.chatbot.backend.models.LogEntity;
import br.com.chatbot.backend.models.MessageEntity;
import br.com.chatbot.backend.models.UserEntity;
import br.com.chatbot.backend.repositories.LogRepository;
import br.com.chatbot.backend.repositories.MessageRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;
    private final LogRepository logRepository;
    private final UserService userService;

    @Autowired
    public MessageServiceImpl(MessageRepository messageRepository,
                              LogRepository logRepository,
                              UserService userService) {
        this.messageRepository = messageRepository;
        this.logRepository = logRepository;
        this.userService = userService;
    }

    @Transactional
    @Override
    public MessageDto createLogByIdAndStatusAndUserId(int id, int status, int userId) {
        Optional<MessageEntity> messageEntity = messageRepository.findById(id);

        MessageEntity entity = messageRepository.save(messageEntity.orElseThrow(() ->
                new IllegalStateException("Mensagem não encontrada")));

        saveLog(entity, createLog(Objects.requireNonNull(MessageLogStatusEnum.fromId(status)), userId));

        return new ModelMapper().map(entity, MessageDto.class);
    }

    private LogEntity createLog(MessageLogStatusEnum messageLogStatusEnum, int userId) {
        Optional<UserEntity> userEntity = userService.findById(userId);
        return new LogEntity(LocalDateTime.now(), messageLogStatusEnum.getValue(), userEntity.orElseThrow(() -> new IllegalStateException("Usuário não encontrado.")));
    }

    private MessageEntity saveLog(MessageEntity entity, LogEntity logEntity) {
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
