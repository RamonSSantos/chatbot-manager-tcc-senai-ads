package br.com.chatbot.backend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private int id;
    private String cpf;
    private String registration;
    private int status;
    private String email;
    private String fullname;
    private String cellphone;

    @JsonIgnore
    private String password;
    private ProfileDto profile;
    private SectorDto sector;
}
