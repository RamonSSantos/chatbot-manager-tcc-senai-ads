package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.models.ProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {
    @Query("select p from ProfileEntity p where p.description like :description%")
    ProfileEntity getByDescription(@Param("description") String description);

    @Query("select p from ProfileEntity p where p.status = 1")
    List<ProfileEntity> getByProfileStatus();
}
