package br.com.chatbot.backend.controllers;

import java.util.List;
import java.util.Optional;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.dtos.UserResponseDto;
import br.com.chatbot.backend.dtos.EditOwnPasswordDto;
import br.com.chatbot.backend.dtos.UserDto;
import br.com.chatbot.backend.models.UserEntity;
import br.com.chatbot.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/user", method = RequestMethod.GET)
@RestController
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Page<UserEntity> getAllUsers(
            @RequestParam(defaultValue = "1") Integer pageNumber,
            @RequestParam(defaultValue = "5") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") Boolean sortDesc
    ) {
        return this.userService.findAll(pageNumber - 1, pageSize, sortBy, sortDesc);
    }

    @GetMapping(value = "/view/{id}")
    public Optional<UserEntity> getById(@PathVariable int id) {
        return this.userService.findById(id);
    }

    @GetMapping(value = "/search")
    public List<UserEntity> getByFullname(@RequestParam String fullname) {
        return this.userService.findByFullname(fullname);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody UserDto userDto) {
        this.userService.save(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Usuário cadastrado com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody UserDto userDto) {
        this.userService.edit(userDto);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário alterado com sucesso!");
    }

    @PutMapping(value = "/edit-user-status/id/{id}/status/{status}")
    public ResponseEntity<String> editUserStatus(@PathVariable int id, @PathVariable int status) {
        this.userService.editUserStatus(id, status);
        return ResponseEntity.status(HttpStatus.OK).body("Usuário alterado com sucesso!");
    }

    @GetMapping(value = "/get-own-user/{id}")
    public UserDto getOwnUser(@PathVariable int id) {
        return this.userService.getOwnUser(id);
    }

    @PutMapping(value = "/edit-own-user")
    public ResponseEntity<String> editOwnUser(@RequestBody UserDto userDto) {
        userService.editOwnUser(userDto);
        return ResponseEntity.status(HttpStatus.OK).body("Dados atualizado com sucesso.");
    }

    @PutMapping(value = "/edit-own-password")
    public ResponseEntity<String> editOwnPassword(@RequestBody EditOwnPasswordDto editOwnPasswordDto) {
        userService.editOwnPassword(editOwnPasswordDto);
        return ResponseEntity.status(HttpStatus.OK).body("Senha alterada com sucesso.");
    }

    @GetMapping(value = "/report/get-all-group-by-sector-description")
    public ResponseEntity<List<BasicReportDto>> getAllGroupBySectorDescription() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllSectorDescriptionAndCountIdGroupBySectorDescription());
    }

    @GetMapping(value = "/report/get-all-group-by-profile-description")
    public ResponseEntity<List<BasicReportDto>> getAllGroupByProfileDescription() {
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllProfileDescriptionAndCountIdGroupByProfileDescription());
    }

    @PostMapping(value = "/create-user")
    public ResponseEntity<UserResponseDto> createUser(@RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(userDto));
    }
}
