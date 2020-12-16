package br.com.chatbot.backend.services;

import br.com.chatbot.backend.dtos.ProfileDto;
import br.com.chatbot.backend.models.ProfileEntity;
import java.util.List;
import java.util.Optional;

public interface ProfileService {
    List<ProfileEntity> findAll();

    Optional<ProfileEntity> findById(Integer id);

    ProfileEntity findByDescription(String description);

    ProfileDto save(ProfileDto profileDto);

    ProfileDto edit(ProfileDto profileDto);

    List<ProfileEntity> getByProfileStatus();
}
