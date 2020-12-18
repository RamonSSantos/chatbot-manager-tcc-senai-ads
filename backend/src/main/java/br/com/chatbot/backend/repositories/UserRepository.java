package br.com.chatbot.backend.repositories;

import br.com.chatbot.backend.dtos.BasicReportDto;
import br.com.chatbot.backend.models.ProfileEntity;
import br.com.chatbot.backend.models.UserEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {
    @Query("select u from UserEntity u where u.fullname like :fullname%")
    List<UserEntity> getByFullname(@Param("fullname") String fullname);

    @Query("select u from UserEntity u where u.email = :email")
    UserEntity getByEmail(@Param("email") String email);

    @Modifying
    @Query("update from UserEntity u set u.status = :status where u.id = :id")
    void editUserStatus(@Param("id") int id, @Param("status") int status);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.cpf = :cpf")
    boolean existsByCpf(@Param("cpf") String cpf);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.registration = :registration")
    boolean existsByRegistration(@Param("registration") String registration);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.email = :email")
    boolean existsByEmail(@Param("email") String email);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.cellphone = :cellphone")
    boolean existsByCellphone(@Param("cellphone") String cellphone);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.id = :id and u.cpf = :cpf")
    boolean existsByIdAndCpf(@Param("id") int id, @Param("cpf") String cpf);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.id = :id and u.registration = :registration")
    boolean existsByIdAndRegistration(@Param("id") int id, @Param("registration") String registration);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.id = :id and u.email = :email")
    boolean existsByIdAndEmail(@Param("id") int id, @Param("email") String email);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.id = :id and u.cellphone = :cellphone")
    boolean existsByIdAndCellphone(@Param("id") int id, @Param("cellphone") String cellphone);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.cpf = :cpf and u.registration = :registration and u.email = :email")
    boolean existsByCpfAndRegistrationAndEmail(@Param("cpf") String cpf, @Param("registration") String registration, @Param("email") String email);

    @Query("select case when count(u) > 0 then true else false end from UserEntity u where u.cpf = :cpf and u.registration = :registration and u.email = :email and u.cellphone = :cellphone")
    boolean existsByCpfAndRegistrationAndEmailAndCellphone(@Param("cpf") String cpf, @Param("registration") String registration, @Param("email") String email, @Param("cellphone") String cellphone);

    @Query("select u from UserEntity u where u.cpf = :cpf")
    UserEntity getByCpf(@Param("cpf") String cpf);

    @Query("select u.profile from UserEntity u where u.id = :id")
    ProfileEntity getProfileById(@Param("id") int id);

    @Modifying
    @Query("update from UserEntity u set u.password = :hashPassword where u.id = :id")
    void resetPassword(@Param("id") int id, @Param("hashPassword") String hashPassword);

    @Query("select new br.com.chatbot.backend.dtos.BasicReportDto(s.description, count(u.id)) "
            + "from UserEntity u "
            + "inner join SectorEntity s on s.id = u.sector.id "
            + "group by s.description order by count(u.id) desc ")
    List<BasicReportDto> getAllSectorDescriptionAndCountIdGroupBySectorDescription(Pageable pageable);

    @Query("select new br.com.chatbot.backend.dtos.BasicReportDto(p.description, count(u.id)) "
            + "from UserEntity u "
            + "inner join ProfileEntity p on p.id = u.profile.id "
            + "group by p.description order by p.description ")
    List<BasicReportDto> getAllProfileDescriptionAndCountIdGroupByProfileDescription(Pageable pageable);
}
