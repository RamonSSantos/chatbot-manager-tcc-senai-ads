package br.com.chatbot.backend.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import br.com.chatbot.backend.dtos.AuthenticationDto;
import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.dtos.ContentDto;
import br.com.chatbot.backend.enums.ContentLogStatusEnum;
import br.com.chatbot.backend.enums.StatusEnum;
import br.com.chatbot.backend.models.*;
import br.com.chatbot.backend.repositories.*;
import br.com.chatbot.backend.utils.UserAuthenticated;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContentServiceImpl implements ContentService {
    private final ContentRepository contentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final KeywordRepository keywordRepository;
    private final LogRepository logRepository;
    private final UserService userService;

    @Autowired
    public ContentServiceImpl(ContentRepository contentRepository,
                              QuestionRepository questionRepository,
                              AnswerRepository answerRepository,
                              KeywordRepository keywordRepository,
                              LogRepository logRepository,
                              UserService userService) {
        this.contentRepository = contentRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.keywordRepository = keywordRepository;
        this.logRepository = logRepository;
        this.userService = userService;
    }

    @Override
    public Page<ContentEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(!sortDesc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return this.contentRepository.findAll(paging);
    }

    @Override
    public Optional<ContentEntity> findById(Integer id) {
        return existsById(id);
    }

    @Override
    public List<ContentEntity> findByTitle(String title) {
        List<ContentEntity> contentEntity = this.contentRepository.getByTitle(title);
        if (contentEntity.isEmpty()) {
            throw new IllegalArgumentException("Conteúdo não encontrado.");
        }

        return contentEntity;
    }

    @Override
    public ContentDto save(ContentDto contentDto) {
        validateContent(contentDto);

        contentDto.setStatus(StatusEnum.ENABLED.getValue());
        ContentEntity contentEntity = new ModelMapper().map(contentDto, ContentEntity.class);

        contentEntity.getQuestion().forEach(questionRepository::save);
        contentEntity.getAnswer().forEach(answerRepository::save);
        contentEntity.getKeyword().forEach(keywordRepository::save);

        ContentEntity entity = contentRepository.save(contentEntity);

        saveLog(entity, createLog(ContentLogStatusEnum.CREATE));

        return new ModelMapper().map(entity, ContentDto.class);
    }

    @Override
    public ContentDto edit(ContentDto contentDto) {
        validateContent(contentDto);

        Optional<ContentEntity> content = existsById(contentDto.getId());
        contentDto.setStatus(content.map(ContentEntity::getStatus).orElse(StatusEnum.DISABLED.getValue()));

        ContentEntity contentEntity = new ModelMapper().map(contentDto, ContentEntity.class);

        updateCreateDeleteQuestions(contentEntity);
        updateCreateDeleteAnswers(contentEntity);
        updateCreateDeleteKeywords(contentEntity);

        saveLog(contentEntity, createLog(ContentLogStatusEnum.EDIT));

        ContentEntity entity = contentRepository.save(contentEntity);

        return new ModelMapper().map(entity, ContentDto.class);
    }

    @Transactional
    @Override
    public void editContentStatus(int id, int status) {
        existsById(id);
        contentRepository.editContentStatus(id, status);
    }

    @Override
    public List<BasicReportDto> getAllTopicDescriptionAndCountIdGroupByTopicDescription() {
        return contentRepository.getAllTopicDescriptionAndCountIdGroupByTopicDescription(PageRequest.of(0, 10));
    }

    @Override
    public List<BasicReportDto> getAllSectorDescriptionAndCountIdGroupBySectorDescription() {
        List<BasicReportDto> basicReportDtos = contentRepository.getAllSectorDescriptionAndCountIdGroupBySectorDescription(PageRequest.of(0, 10));

        for (BasicReportDto basicReportDto : basicReportDtos) {
            basicReportDto.setDescription(basicReportDto.getDescription() == null ? "Não possui setor" : basicReportDto.getDescription());
        }

        return basicReportDtos;
    }

    private Optional<ContentEntity> existsById(int id) {
        Optional<ContentEntity> content = contentRepository.findById(id);
        if (!content.isPresent()) {
            throw new NullPointerException("Conteúdo não encontrado.");
        }

        return content;
    }

    private void validateContent(ContentDto contentDto) {
        Objects.requireNonNull(contentDto.getTitle(), "Informe o título");
        Objects.requireNonNull(contentDto.getTopic(), "Informe o tema");

        if (contentDto.getAnswer().isEmpty() && contentDto.getAttachment() == null) {
            throw new IllegalStateException("Resposta ou palavra-chave é obrigatório.");
        }

        if (!contentDto.getQuestion().isEmpty()) {
            if (contentDto.getKeyword().isEmpty()) {
                throw new IllegalStateException("Informe alguma palavra-chave.");
            }
        }

        if (contentDto.getQuestion().isEmpty() && contentDto.getSector() == null) {
            throw new IllegalStateException("Pergunta ou setor é obrigatório.");
        }
    }

    private LogEntity createLog(ContentLogStatusEnum contentLogStatusEnum) {
        AuthenticationDto authenticationDto = UserAuthenticated.getUserAuthenticated();
        Optional<UserEntity> userEntity = userService.findById(authenticationDto.getUserId());

        return new LogEntity(LocalDateTime.now(), contentLogStatusEnum.getValue(), userEntity.orElse(null));
    }

    private void saveLog(ContentEntity entity, LogEntity logEntity) {
        if (entity.getLog() == null) {
            List<LogEntity> logEntities = new ArrayList<>();
            logEntities.add(logEntity);

            entity.setLog(logEntities);
        } else {
            entity.getLog().add(logEntity);
        }

        entity.getLog().forEach(logRepository::save);
    }

    private void updateCreateDeleteQuestions(ContentEntity contentEntity) {
        List<QuestionEntity> questions = contentRepository.getAllQuestionsByContentId(contentEntity.getId());

        List<QuestionEntity> newQuestions = new ArrayList<>();
        for (QuestionEntity question : contentEntity.getQuestion()) {
            boolean isSameQuestion = questions.contains(question);
            if (question.getId() == 0 || !isSameQuestion) {
                newQuestions.add(question);
            }
        }

        List<QuestionEntity> oldQuestions = new ArrayList<>();
        for (QuestionEntity question : questions) {
            boolean isDifferentQuestion = contentEntity.getQuestion().contains(question);
            if (!isDifferentQuestion) {
                oldQuestions.add(question);
            }
        }

        if (!newQuestions.isEmpty() && !oldQuestions.isEmpty()) {
            if (newQuestions.size() == oldQuestions.size() || oldQuestions.equals(questions)) {
                oldQuestions.forEach(question -> question.setDescription(newQuestions.stream().map(QuestionEntity::getDescription).findFirst().orElse(null)));
                oldQuestions.forEach(questionRepository::save);

                contentEntity.getQuestion().removeIf(question -> oldQuestions.stream().anyMatch(q -> q.getDescription().equals(question.getDescription())));
                contentEntity.getQuestion().addAll(oldQuestions);
            } else {
                List<QuestionEntity> updateQuestions;
                if (newQuestions.size() < oldQuestions.size()) {
                    IntStream.range(0, newQuestions.size()).forEach(i -> oldQuestions.get(i).setDescription(newQuestions.stream().map(QuestionEntity::getDescription).findFirst().orElse(null)));
                    List<QuestionEntity> deleteQuestions = oldQuestions.stream().filter(question -> newQuestions.stream().anyMatch(q -> !q.getDescription().equals(question.getDescription()))).collect(Collectors.toList());
                    deleteQuestions.forEach(questionRepository::delete);
                } else {
                    IntStream.range(0, oldQuestions.size()).forEach(i -> oldQuestions.get(i).setDescription(newQuestions.stream().map(QuestionEntity::getDescription).findFirst().orElse(null)));
                }

                updateQuestions = oldQuestions.stream().filter(question -> newQuestions.stream().anyMatch(q -> q.getDescription().equals(question.getDescription()))).collect(Collectors.toList());
                newQuestions.removeIf(question -> updateQuestions.stream().anyMatch(q -> q.getDescription().equals(question.getDescription())));

                updateQuestions.forEach(questionRepository::save);
                newQuestions.forEach(questionRepository::save);

                contentEntity.getQuestion().removeIf(question -> updateQuestions.stream().anyMatch(q -> q.getDescription().equals(question.getDescription())));
                contentEntity.getQuestion().addAll(updateQuestions);
            }
        } else if (!oldQuestions.isEmpty()) {
            oldQuestions.forEach(questionRepository::delete);
        }
    }

    private void updateCreateDeleteAnswers(ContentEntity contentEntity) {
        List<AnswerEntity> answers = contentRepository.getAllAnswersByContentId(contentEntity.getId());

        List<AnswerEntity> newAnswers = new ArrayList<>();
        for (AnswerEntity answer : contentEntity.getAnswer()) {
            boolean isSameAnswer = answers.contains(answer);
            if (answer.getId() == 0 || !isSameAnswer) {
                newAnswers.add(answer);
            }
        }

        List<AnswerEntity> oldAnswers = new ArrayList<>();
        for (AnswerEntity answer : answers) {
            boolean isDifferentAnswer = contentEntity.getAnswer().contains(answer);
            if (!isDifferentAnswer) {
                oldAnswers.add(answer);
            }
        }

        if (!newAnswers.isEmpty() && !oldAnswers.isEmpty()) {
            if (newAnswers.size() == oldAnswers.size() || oldAnswers.equals(answers)) {
                oldAnswers.forEach(answer -> answer.setDescription(newAnswers.stream().map(AnswerEntity::getDescription).findFirst().orElse(null)));
                oldAnswers.forEach(answerRepository::save);

                contentEntity.getAnswer().removeIf(answer -> oldAnswers.stream().anyMatch(a -> a.getDescription().equals(answer.getDescription())));
                contentEntity.getAnswer().addAll(oldAnswers);
            } else {
                List<AnswerEntity> updateAnswers;
                if (newAnswers.size() < oldAnswers.size()) {
                    IntStream.range(0, newAnswers.size()).forEach(i -> oldAnswers.get(i).setDescription(newAnswers.stream().map(AnswerEntity::getDescription).findFirst().orElse(null)));
                    List<AnswerEntity> deleteAnswers = oldAnswers.stream().filter(answer -> newAnswers.stream().anyMatch(a -> !a.getDescription().equals(answer.getDescription()))).collect(Collectors.toList());
                    deleteAnswers.forEach(answerRepository::delete);
                } else {
                    IntStream.range(0, oldAnswers.size()).forEach(i -> oldAnswers.get(i).setDescription(newAnswers.stream().map(AnswerEntity::getDescription).findFirst().orElse(null)));
                }

                updateAnswers = oldAnswers.stream().filter(answer -> newAnswers.stream().anyMatch(a -> a.getDescription().equals(answer.getDescription()))).collect(Collectors.toList());
                newAnswers.removeIf(answer -> updateAnswers.stream().anyMatch(a -> a.getDescription().equals(answer.getDescription())));

                updateAnswers.forEach(answerRepository::save);
                newAnswers.forEach(answerRepository::save);

                contentEntity.getAnswer().removeIf(answer -> updateAnswers.stream().anyMatch(a -> a.getDescription().equals(answer.getDescription())));
                contentEntity.getAnswer().addAll(updateAnswers);
            }
        } else if (!oldAnswers.isEmpty()) {
            oldAnswers.forEach(answerRepository::delete);
        }
    }

    private void updateCreateDeleteKeywords(ContentEntity contentEntity) {
        List<KeywordEntity> keywords = contentRepository.getAllKeywordsByContentId(contentEntity.getId());

        List<KeywordEntity> newKeywords = new ArrayList<>();
        for (KeywordEntity keyword : contentEntity.getKeyword()) {
            boolean isSameKeyword = keywords.contains(keyword);
            if (keyword.getId() == 0 || !isSameKeyword) {
                newKeywords.add(keyword);
            }
        }

        List<KeywordEntity> oldKeywords = new ArrayList<>();
        for (KeywordEntity keyword : keywords) {
            boolean isDifferentKeyword = contentEntity.getKeyword().contains(keyword);
            if (!isDifferentKeyword) {
                oldKeywords.add(keyword);
            }
        }

        if (!newKeywords.isEmpty() && !oldKeywords.isEmpty()) {
            if (newKeywords.size() == oldKeywords.size() || oldKeywords.equals(keywords)) {
                oldKeywords.forEach(keyword -> keyword.setDescription(newKeywords.stream().map(KeywordEntity::getDescription).findFirst().orElse(null)));
                oldKeywords.forEach(keywordRepository::save);

                contentEntity.getKeyword().removeIf(keyword -> oldKeywords.stream().anyMatch(k -> k.getDescription().equals(keyword.getDescription())));
                contentEntity.getKeyword().addAll(oldKeywords);
            } else {
                List<KeywordEntity> updateKeywords;
                if (newKeywords.size() < oldKeywords.size()) {
                    IntStream.range(0, newKeywords.size()).forEach(i -> oldKeywords.get(i).setDescription(newKeywords.stream().map(KeywordEntity::getDescription).findFirst().orElse(null)));
                    List<KeywordEntity> deleteKeywords = oldKeywords.stream().filter(keyword -> newKeywords.stream().anyMatch(k -> !k.getDescription().equals(keyword.getDescription()))).collect(Collectors.toList());
                    deleteKeywords.forEach(keywordRepository::delete);
                } else {
                    IntStream.range(0, oldKeywords.size()).forEach(i -> oldKeywords.get(i).setDescription(newKeywords.stream().map(KeywordEntity::getDescription).findFirst().orElse(null)));
                }

                updateKeywords = oldKeywords.stream().filter(keyword -> newKeywords.stream().anyMatch(k -> k.getDescription().equals(keyword.getDescription()))).collect(Collectors.toList());
                newKeywords.removeIf(keyword -> updateKeywords.stream().anyMatch(k -> k.getDescription().equals(keyword.getDescription())));

                updateKeywords.forEach(keywordRepository::save);
                newKeywords.forEach(keywordRepository::save);

                contentEntity.getKeyword().removeIf(keyword -> updateKeywords.stream().anyMatch(k -> k.getDescription().equals(keyword.getDescription())));
                contentEntity.getKeyword().addAll(updateKeywords);
            }
        } else if (!oldKeywords.isEmpty()) {
            oldKeywords.forEach(keywordRepository::delete);
        }
    }
}
