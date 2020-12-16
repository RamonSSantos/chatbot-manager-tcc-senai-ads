package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.*;
import br.com.chatbot.backend.models.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService {
    Page<UserEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc);

    Optional<UserEntity> findById(Integer id);

    UserEntity findByEmail(String email);

    List<UserEntity> findByFullname(String description);

    UserDto save(UserDto userDto);

    UserDto edit(UserDto userDto);

    void editUserStatus(int id, int status);

    void forgotPassword(ForgotPasswordDto forgetPasswordDto);

    void resetPassword(ForgotPasswordDto forgetPasswordDto);

    UserDto getOwnUser(int userId);

    UserDto editOwnUser(UserDto userDto);

    void editOwnPassword(EditOwnPasswordDto editOwnPasswordDto);

    List<BasicReportDto> getAllSectorDescriptionAndCountIdGroupBySectorDescription();

    List<BasicReportDto> getAllProfileDescriptionAndCountIdGroupByProfileDescription();

    UserResponseDto createUser(UserDto userDto);

    Collection<? extends GrantedAuthority> getAuthorities(int userId);
}
