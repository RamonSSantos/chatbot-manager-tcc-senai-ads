package br.com.chatbot.backend.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "authentication")
public class AuthenticationEntity {
    @Id
    @Column(name = "token",nullable = false)
    private String token;

    @Column(name = "user_id", nullable = false)
    private int userId;

    @Column(name = "user_fullname", nullable = false)
    private String userFullname;

    @Column(name = "user_email", nullable = false)
    private String userEmail;

    @Column(name = "starting_date", nullable = false)
    private LocalDateTime startingDate;

    @Column(name = "ending_date", nullable = false)
    private LocalDateTime endingDate;
}
