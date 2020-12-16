package br.com.chatbot.backend.services;

import java.util.List;
import java.util.Optional;
import br.com.chatbot.backend.dtos.ProfileDto;
import br.com.chatbot.backend.models.ProfileEntity;
import br.com.chatbot.backend.repositories.ProfileRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository profileRepository;

    @Autowired
    public ProfileServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public List<ProfileEntity> findAll() {
        return this.profileRepository.findAll();
    }

    @Override
    public Optional<ProfileEntity> findById(Integer id) {
        return this.profileRepository.findById(id);
    }

    @Override
    public ProfileEntity findByDescription(String description) {
        return this.profileRepository.getByDescription(description);
    }

    @Override
    public ProfileDto save(ProfileDto profileDto) {
        ProfileEntity profileEntity = new ProfileEntity();
        BeanUtils.copyProperties(profileDto, profileEntity);

        ProfileEntity entity = this.profileRepository.save(profileEntity);
        ProfileDto dto = new ProfileDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public ProfileDto edit(ProfileDto profileDto) {
        ProfileEntity profileEntity = new ProfileEntity();
        BeanUtils.copyProperties(profileDto, profileEntity);

        ProfileEntity entity = this.profileRepository.save(profileEntity);
        ProfileDto dto = new ProfileDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    @Override
    public List<ProfileEntity> getByProfileStatus() {
        return this.profileRepository.getByProfileStatus();
    }
}
