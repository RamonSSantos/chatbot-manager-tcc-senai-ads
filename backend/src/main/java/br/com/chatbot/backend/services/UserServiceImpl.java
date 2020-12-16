package br.com.chatbot.backend.services;

import java.security.AccessControlException;
import java.util.*;
import br.com.chatbot.backend.dtos.*;
import br.com.chatbot.backend.enums.MethodEnum;
import br.com.chatbot.backend.enums.ProfileEnum;
import br.com.chatbot.backend.enums.StatusEnum;
import br.com.chatbot.backend.models.ProfileEntity;
import br.com.chatbot.backend.models.UserEntity;
import br.com.chatbot.backend.repositories.UserRepository;
import br.com.chatbot.backend.utils.BCryptEncoder;
import br.com.chatbot.backend.utils.UserAuthenticated;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<UserEntity> findAll(Integer pageNumber, Integer pageSize, String sortBy, boolean sortDesc) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(!sortDesc ? Sort.Direction.ASC : Sort.Direction.DESC, sortBy));
        return this.userRepository.findAll(paging);
    }

    @Override
    public Optional<UserEntity> findById(Integer id) {
        return existsById(id);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.getByEmail(email);
    }

    @Override
    public List<UserEntity> findByFullname(String fullname) {
        List<UserEntity> userEntity = this.userRepository.getByFullname(fullname);
        if (userEntity.isEmpty()) {
            throw new IllegalArgumentException("Usuário não encontrado.");
        }

        return userEntity;
    }

    @Override
    public UserDto save(UserDto userDto) {
        checkNullValues(userDto);

        validateCpf(MethodEnum.SAVE, userDto.getId(), userDto.getCpf());
        validateRegistration(MethodEnum.SAVE, userDto.getId(), userDto.getRegistration());
        validateEmail(MethodEnum.SAVE, userDto.getId(), userDto.getEmail());
        validateCellphone(MethodEnum.SAVE, userDto.getId(), userDto.getCellphone());

        userDto.setStatus(StatusEnum.ENABLED.getValue());

        if (userDto.getProfile().getId() != ProfileEnum.EMPLOYEE.getValue()) {
            BCryptEncoder passwordEncoder = new BCryptEncoder();
            userDto.setPassword(passwordEncoder.encode(userDto.getCpf()));
        }

        UserEntity userEntity = new ModelMapper().map(userDto, UserEntity.class);
        UserEntity entity = userRepository.save(userEntity);

        return new ModelMapper().map(entity, UserDto.class);
    }

    @Override
    public UserDto edit(UserDto userDto) {
        checkNullValues(userDto);
        Optional<UserEntity> user = existsById(userDto.getId());

        validateCpf(MethodEnum.EDIT, userDto.getId(), userDto.getCpf());
        validateRegistration(MethodEnum.EDIT, userDto.getId(), userDto.getRegistration());
        validateEmail(MethodEnum.EDIT, userDto.getId(), userDto.getEmail());
        validateCellphone(MethodEnum.EDIT, userDto.getId(), userDto.getCellphone());

        userDto.setStatus(user.map(UserEntity::getStatus).orElse(StatusEnum.DISABLED.getValue()));

        BCryptEncoder passwordEncoder = new BCryptEncoder();
        String hashPassword = user.map(UserEntity::getPassword).orElse(null);
        userDto.setPassword(hashPassword != null ? hashPassword : passwordEncoder.encode(userDto.getCpf()));

        UserEntity userEntity = new ModelMapper().map(userDto, UserEntity.class);
        UserEntity entity = userRepository.save(userEntity);

        return new ModelMapper().map(entity, UserDto.class);
    }

    @Transactional
    @Override
    public void editUserStatus(int id, int status) {
        Optional<UserEntity> userEntity = existsById(id);
        userEntity.ifPresent(user -> {
            if (StatusEnum.APPROVAL.getValue() == user.getStatus() && user.getSector() == null) {
                throw new IllegalStateException("Adicione algum setor para o usuário.");
            }
        });
        userRepository.editUserStatus(id, status);
    }

    @Override
    public void forgotPassword(ForgotPasswordDto forgotPasswordDto) {
        checkNullValuesForgotPassword(forgotPasswordDto);
        validateCpfAndRegistrationAndEmail(forgotPasswordDto);

        UserEntity userEntity = userRepository.getByCpf(forgotPasswordDto.getCpf());

        if (StatusEnum.DISABLED.getValue() == userEntity.getStatus()) {
            throw new IllegalStateException("Usuário não está ativo.");
        }

        if (ProfileEnum.EMPLOYEE.getValue() == userEntity.getProfile().getId()) {
            throw new AccessControlException("Acesso negado.");
        }
    }

    @Transactional
    @Override
    public void resetPassword(ForgotPasswordDto forgotPasswordDto) {
        checkNullValuesForgotPassword(forgotPasswordDto);
        Objects.requireNonNull(forgotPasswordDto.getPassword(), "Informe a senha.");
        Objects.requireNonNull(forgotPasswordDto.getPasswordConfirmation(), "Informe a confirmação da senha.");

        validateCpfAndRegistrationAndEmail(forgotPasswordDto);

        UserEntity userEntity = userRepository.getByCpf(forgotPasswordDto.getCpf());

        if (StatusEnum.DISABLED.getValue() == userEntity.getStatus()) {
            throw new IllegalStateException("Usuário não está ativo.");
        }

        if (ProfileEnum.EMPLOYEE.getValue() == userEntity.getProfile().getId()) {
            throw new AccessControlException("Acesso negado.");
        }

        BCryptEncoder bCryptEncoder = new BCryptEncoder();
        String hashPassword = bCryptEncoder.encode(forgotPasswordDto.getPassword());

        if (!bCryptEncoder.matches(forgotPasswordDto.getPasswordConfirmation(), hashPassword)) {
            throw new IllegalStateException("As senhas não correspondem.");
        }

        userRepository.resetPassword(userEntity.getId(), hashPassword);
    }

    @Override
    public UserDto getOwnUser(int userId) {
        AuthenticationDto authenticationDto = UserAuthenticated.getUserAuthenticated();
        Optional<UserEntity> user = userRepository.findById(authenticationDto.getUserId());

        int idUserAuthenticated = user.map(UserEntity::getId).orElseThrow(() -> new BadCredentialsException("Autenticação inválida."));
        if (userId != idUserAuthenticated) {
            throw new AccessControlException("Acesso negado.");
        }

        return new ModelMapper().map(user.get(), UserDto.class);
    }

    @Transactional
    @Override
    public UserDto editOwnUser(UserDto userDto) {
        AuthenticationDto authenticationDto = UserAuthenticated.getUserAuthenticated();
        Optional<UserEntity> user = userRepository.findById(authenticationDto.getUserId());

        int idUserAuthenticated = user.map(UserEntity::getId).orElseThrow(() -> new BadCredentialsException("Autenticação inválida."));
        if (userDto.getId() != idUserAuthenticated) {
            throw new AccessControlException("Acesso negado.");
        }

        checkNullValues(userDto);
        validateEmail(MethodEnum.EDIT, userDto.getId(), userDto.getEmail());
        validateCellphone(MethodEnum.EDIT, userDto.getId(), userDto.getCellphone());

        userDto.setStatus(user.map(UserEntity::getStatus).orElse(StatusEnum.ENABLED.getValue()));
        userDto.setPassword(user.map(UserEntity::getPassword).orElse(null));

        UserEntity userEntity = new ModelMapper().map(userDto, UserEntity.class);

        userEntity.setCpf(user.map(UserEntity::getCpf).orElse(null));
        userEntity.setRegistration(user.map(UserEntity::getRegistration).orElse(null));
        userEntity.setProfile(user.map(UserEntity::getProfile).orElse(null));
        userEntity.setSector(user.map(UserEntity::getSector).orElse(null));

        UserEntity entity = userRepository.save(userEntity);

        return new ModelMapper().map(entity, UserDto.class);
    }

    @Transactional
    @Override
    public void editOwnPassword(EditOwnPasswordDto editOwnPasswordDto) {
        AuthenticationDto authenticationDto = UserAuthenticated.getUserAuthenticated();
        Optional<UserEntity> user = userRepository.findById(authenticationDto.getUserId());

        int idUserAuthenticated = user.map(UserEntity::getId).orElseThrow(() -> new BadCredentialsException("Autenticação inválida."));
        if (editOwnPasswordDto.getUserId() != idUserAuthenticated) {
            throw new AccessControlException("Acesso negado.");
        }

        Objects.requireNonNull(editOwnPasswordDto.getOldPassword(), "Informe a senha atual.");
        Objects.requireNonNull(editOwnPasswordDto.getNewPassword(), "Informe a nova senha.");
        Objects.requireNonNull(editOwnPasswordDto.getNewPasswordConfirmation(), "Informe a confirmação da nova senha.");

        BCryptEncoder bCryptEncoder = new BCryptEncoder();

        String oldPasswordHash = user.map(UserEntity::getPassword).orElse(null);
        if (!bCryptEncoder.matches(editOwnPasswordDto.getOldPassword(), oldPasswordHash)) {
            throw new IllegalStateException("A senha atual está incorreta.");
        }

        String hashPassword = bCryptEncoder.encode(editOwnPasswordDto.getNewPassword());
        if (!bCryptEncoder.matches(editOwnPasswordDto.getNewPasswordConfirmation(), hashPassword)) {
            throw new IllegalStateException("As senhas não correspondem.");
        }

        userRepository.resetPassword(editOwnPasswordDto.getUserId(), hashPassword);
    }

    @Override
    public List<BasicReportDto> getAllSectorDescriptionAndCountIdGroupBySectorDescription() {
        return userRepository.getAllSectorDescriptionAndCountIdGroupBySectorDescription();
    }

    @Override
    public List<BasicReportDto> getAllProfileDescriptionAndCountIdGroupByProfileDescription() {
        return userRepository.getAllProfileDescriptionAndCountIdGroupByProfileDescription();
    }

    @Override
    public UserResponseDto createUser(UserDto userDto) {
        Objects.requireNonNull(userDto.getCpf(), "Informe o CPF.");
        Objects.requireNonNull(userDto.getRegistration(), "Informe a matrícula.");
        Objects.requireNonNull(userDto.getEmail(), "Informe o e-mail.");
        Objects.requireNonNull(userDto.getCellphone(), "Informe o número de celular.");
        Objects.requireNonNull(userDto.getFullname(), "Informe o nome completo.");

        if (userRepository.existsByCpfAndRegistrationAndEmailAndCellphone(userDto.getCpf(), userDto.getRegistration(),
                userDto.getEmail(), userDto.getCellphone())) {
            UserEntity userEntity = userRepository.getByCpf(userDto.getCpf());

            return createUserResponseDto(userEntity);
        }

        validateCpf(MethodEnum.SAVE, userDto.getId(), userDto.getCpf());
        validateRegistration(MethodEnum.SAVE, userDto.getId(), userDto.getRegistration());
        validateEmail(MethodEnum.SAVE, userDto.getId(), userDto.getEmail());
        validateCellphone(MethodEnum.SAVE, userDto.getId(), userDto.getCellphone());

        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(ProfileEnum.EMPLOYEE.getValue());

        userDto.setProfile(profileDto);
        userDto.setStatus(StatusEnum.APPROVAL.getValue());

        UserEntity userEntity = new ModelMapper().map(userDto, UserEntity.class);
        UserEntity entity = userRepository.save(userEntity);

        return createUserResponseDto(entity);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(int userId) {
        ProfileEntity profileEntity = userRepository.getProfileById(userId);

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(profileEntity.getDescription()));

        return authorities;
    }

    private Optional<UserEntity> existsById(int id) {
        Optional<UserEntity> user = userRepository.findById(id);
        if (!user.isPresent()) {
            throw new NullPointerException("Usuário não encontrado.");
        }

        return user;
    }

    private void checkNullValues(UserDto userDto) {
        Objects.requireNonNull(userDto.getCpf(), "Informe o CPF.");
        Objects.requireNonNull(userDto.getRegistration(), "Informe a matrícula.");
        Objects.requireNonNull(userDto.getEmail(), "Informe o e-mail.");
        Objects.requireNonNull(userDto.getCellphone(), "Informe o número de celular.");
        Objects.requireNonNull(userDto.getFullname(), "Informe o nome completo.");
        Objects.requireNonNull(userDto.getProfile(), "Informe o perfil.");
        Objects.requireNonNull(userDto.getSector(), "Informe o setor.");
    }

    private void checkNullValuesForgotPassword(ForgotPasswordDto forgotPasswordDto) {
        Objects.requireNonNull(forgotPasswordDto.getCpf(), "Informe o CPF.");
        Objects.requireNonNull(forgotPasswordDto.getRegistration(), "Informe a matrícula.");
        Objects.requireNonNull(forgotPasswordDto.getEmail(), "Informe o e-mail.");
    }

    private void validateCpf(MethodEnum methodEnum, int id, String cpf) {
        if (cpf.length() > 11) {
            throw new IllegalStateException("CPF inválido.");
        }

        if (MethodEnum.SAVE.equals(methodEnum)) {
            if (userRepository.existsByCpf(cpf)) {
                throw new IllegalArgumentException("Este CPF já existe.");
            }
        }

        if (MethodEnum.EDIT.equals(methodEnum)) {
            if (!userRepository.existsByIdAndCpf(id, cpf)) {
                if (userRepository.existsByCpf(cpf)) {
                    throw new IllegalArgumentException("Este CPF já existe.");
                }
            }
        }
    }

    private void validateRegistration(MethodEnum methodEnum, int id, String registration) {
        if (registration.length() > 8) {
            throw new IllegalStateException("Matrícula inválido.");
        }

        if (MethodEnum.SAVE.equals(methodEnum)) {
            if (userRepository.existsByRegistration(registration)) {
                throw new IllegalArgumentException("Essa matrícula já existe.");
            }
        }

        if (MethodEnum.EDIT.equals(methodEnum)) {
            if (!userRepository.existsByIdAndRegistration(id, registration)) {
                if (userRepository.existsByRegistration(registration)) {
                    throw new IllegalArgumentException("Essa matrícula já existe.");
                }
            }
        }
    }

    private void validateEmail(MethodEnum methodEnum, int id, String email) {
        if (MethodEnum.SAVE.equals(methodEnum)) {
            if (userRepository.existsByEmail(email)) {
                throw new IllegalArgumentException("Este e-mail já existe.");
            }
        }

        if (MethodEnum.EDIT.equals(methodEnum)) {
            if (!userRepository.existsByIdAndEmail(id, email)) {
                if (userRepository.existsByEmail(email)) {
                    throw new IllegalArgumentException("Este e-mail já existe.");
                }
            }
        }
    }

    private void validateCellphone(MethodEnum methodEnum, int id, String cellphone) {
        if (MethodEnum.SAVE.equals(methodEnum)) {
            if (userRepository.existsByCellphone(cellphone)) {
                throw new IllegalArgumentException("Este número de celular já existe.");
            }
        }

        if (MethodEnum.EDIT.equals(methodEnum)) {
            if (!userRepository.existsByIdAndCellphone(id, cellphone)) {
                if (userRepository.existsByCellphone(cellphone)) {
                    throw new IllegalArgumentException("Este número de celular já existe.");
                }
            }
        }
    }

    private void validateCpfAndRegistrationAndEmail(ForgotPasswordDto forgotPasswordDto) {
        if (!userRepository.existsByCpfAndRegistrationAndEmail(forgotPasswordDto.getCpf(),
                forgotPasswordDto.getRegistration(), forgotPasswordDto.getEmail())) {
            throw new IllegalArgumentException("Os dados não correspondem.");
        }
    }

    private UserResponseDto createUserResponseDto(UserEntity userEntity) {
        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(userEntity.getId());
        userResponseDto.setRegistration(userEntity.getRegistration());
        userResponseDto.setStatus(userEntity.getStatus());
        userResponseDto.setFullname(userEntity.getFullname());

        return userResponseDto;
    }
}
