package br.com.chatbot.backend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDto {
    private int id;
    private String title;
    private int status;
    private String description;
    private String attachment;
    private TopicDto topic;
    private SectorDto sector;
    private List<QuestionDto> question;
    private List<AnswerDto> answer;
    private List<KeywordDto> keyword;
    private List<LogDto> log;
}
