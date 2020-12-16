package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.AuthenticationDto;
import br.com.chatbot.backend.dtos.AuthenticationRequestDto;
import br.com.chatbot.backend.enums.ProfileEnum;
import br.com.chatbot.backend.enums.StatusEnum;
import br.com.chatbot.backend.models.AuthenticationEntity;
import br.com.chatbot.backend.models.UserEntity;
import br.com.chatbot.backend.repositories.AuthenticationRepository;
import br.com.chatbot.backend.utils.BCryptEncoder;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;
import java.security.AccessControlException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AuthenticationServiceImpl implements AuthenticationService{
    private final AuthenticationRepository authenticationRepository;
    private final UserService userService;

    @Autowired
    public AuthenticationServiceImpl(AuthenticationRepository authenticationRepository, UserService userService) {
        this.authenticationRepository = authenticationRepository;
        this.userService = userService;
    }

    @Override
    public AuthenticationDto authenticate(AuthenticationRequestDto requestDto) {
        Objects.requireNonNull(requestDto, "Request é obrigatório.");

        UserEntity userEntity = userService.findByEmail(requestDto.getEmail());
        if (userEntity == null) {
            throw new IllegalStateException("Usuário não encontrado.");
        }

        if (userEntity.getStatus() != StatusEnum.ENABLED.getValue()) {
            throw new IllegalStateException("Usuário não está ativo.");
        }

        if (userEntity.getProfile().getId() == ProfileEnum.EMPLOYEE.getValue()) {
            throw new AccessControlException("Acesso negado.");
        }

        BCryptEncoder bCryptEncoder = new BCryptEncoder();
        if (!bCryptEncoder.matches(requestDto.getPassword(), userEntity.getPassword())) {
            throw new IllegalStateException("Senha incorreta.");
        }

        UUID token = UUID.randomUUID();
        AuthenticationEntity authenticationEntity = new AuthenticationEntity();

        authenticationEntity.setToken(token.toString());
        authenticationEntity.setUserId(userEntity.getId());
        authenticationEntity.setUserFullname(userEntity.getFullname());
        authenticationEntity.setUserEmail(userEntity.getEmail());

        LocalDateTime now = getFormattedDate();
        authenticationEntity.setStartingDate(now);
        authenticationEntity.setEndingDate(now.plusHours(3));

        authenticationRepository.save(authenticationEntity);

        AuthenticationDto authenticationDto = new AuthenticationDto();
        BeanUtils.copyProperties(authenticationEntity, authenticationDto);

        // Create Authentication
        PreAuthenticatedAuthenticationToken authentication = new PreAuthenticatedAuthenticationToken(authenticationDto,
                userEntity.getPassword(), userService.getAuthorities(authenticationDto.getUserId()));

        authentication.setDetails(authenticationDto);
        authentication.setAuthenticated(true);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authenticationDto;
    }

    @Override
    public AuthenticationDto validateCookie(String token) {
        Optional<AuthenticationEntity> authenticationEntity = authenticationRepository.findById(token);
        if (!authenticationEntity.isPresent()) {
            throw new BadCredentialsException("Autenticação inválida.");
        }

        // Autenticação expirou
        boolean authenticationHasExpired = authenticationEntity.filter(authentication ->
                LocalDateTime.now().isAfter(authentication.getEndingDate())).isPresent();
        if (authenticationHasExpired) {
            throw new BadCredentialsException("Autenticação expirada.");
        }

        return new ModelMapper().map(authenticationEntity.get(), AuthenticationDto.class);
    }

    @Override
    public void logout(String token) {
        Objects.requireNonNull(token, "Informe o token.");

        Optional<AuthenticationEntity> authenticationEntity = authenticationRepository.findById(token);
        if (!authenticationEntity.isPresent()) {
            throw new BadCredentialsException("Autenticação inválida.");
        }

        AuthenticationEntity entity = authenticationEntity.orElse(null);
        authenticationRepository.delete(entity);
    }

    private LocalDateTime getFormattedDate() {
        DateTimeFormatter formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        LocalDateTime now = LocalDateTime.now();
        String dateString = formatted.format(now);
        return LocalDateTime.parse(dateString, formatted);
    }
}
