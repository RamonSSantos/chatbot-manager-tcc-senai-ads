package br.com.chatbot.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "message")
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "actor", nullable = false, length = 1)
    private int actor;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "content_id", nullable = false)
    private int contentId;

    @Column(name = "question_id")
    private int questionId;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    @JoinTable(name = "message_has_log",
            joinColumns = {@JoinColumn(name = "message_id")},
            inverseJoinColumns = {@JoinColumn(name = "log_id")})
    private List<LogEntity> log = new ArrayList<>();
}
