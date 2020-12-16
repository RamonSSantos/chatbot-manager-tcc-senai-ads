package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.dtos.ContentDto;
import br.com.chatbot.backend.models.ContentEntity;
import org.springframework.data.domain.Page;
import java.util.List;
import java.util.Optional;

public interface ContentService {
    Page<ContentEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc);

    Optional<ContentEntity> findById(Integer id);

    List<ContentEntity> findByTitle(String title);

    ContentDto save(ContentDto contentDto);

    ContentDto edit(ContentDto contentDto);

    void editContentStatus(int id, int status);

    List<BasicReportDto> getAllTopicDescriptionAndCountIdGroupByTopicDescription();

    List<BasicReportDto> getAllSectorDescriptionAndCountIdGroupBySectorDescription();
}
