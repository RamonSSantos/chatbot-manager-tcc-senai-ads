package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.ProfileDto;
import br.com.chatbot.backend.models.ProfileEntity;
import br.com.chatbot.backend.services.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RequestMapping(value = "/api/profile", method = RequestMethod.GET)
@RestController
public class ProfileController {
    private final ProfileService profileService;

    @Autowired
    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping
    public List<ProfileEntity> getAllProfiles() {
        return this.profileService.findAll();
    }

    @GetMapping(value = "/view/{id}")
    public Optional<ProfileEntity> getById(@PathVariable int id) {
        return this.profileService.findById(id);
    }

    @GetMapping(value = "/search")
    public ProfileEntity getByDescription(@RequestParam String description) {
        return this.profileService.findByDescription(description);
    }

    @PostMapping(value = "/new")
    public ResponseEntity<String> save(@RequestBody ProfileDto profileDto) {
        this.profileService.save(profileDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Perfil cadastrado com sucesso!");
    }

    @PutMapping(value = "/edit")
    public ResponseEntity<String> edit(@RequestBody ProfileDto profileDto) {
        this.profileService.edit(profileDto);
        return ResponseEntity.status(HttpStatus.OK).body("Perfil alterado com sucesso!");
    }

    @GetMapping(value = "/get-all-active-profiles")
    public List<ProfileEntity> getAllActiveProfiles() {
        return profileService.getByProfileStatus();
    }
}
