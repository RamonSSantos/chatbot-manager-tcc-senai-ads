package br.com.chatbot.backend.models;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "cpf", nullable = false, length = 11)
    private String cpf;

    @Column(name = "registration", nullable = false, length = 8)
    private String registration;

    @Column(name = "status", nullable = false, length = 1)
    private int status;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "fullname", nullable = false)
    private String fullname;

    @Column(name = "cellphone", nullable = false, length = 11)
    private String cellphone;

    @JsonIgnore
    @Column(name = "password", length = 60)
    private String password;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private ProfileEntity profile;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private SectorEntity sector;
}
