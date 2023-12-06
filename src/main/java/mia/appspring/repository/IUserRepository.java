package mia.appspring.repository;

import jakarta.transaction.Transactional;
import mia.appspring.model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IUserRepository extends CrudRepository<User, Integer> {

    /*
    https://www.baeldung.com/spring-data-partial-update
    https://www.baeldung.com/spring-data-jpa-modifying-annotation
     */

    public User getUserById(Long id);

    public Optional<User> getUserByEmail(String email);

    /*
    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.email = :newEmail WHERE u.id = :id")
    public void updateUserEmail(@Param(value = "id")Long id,
                                @Param(value = "newEmail") String newEmail);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.password = :newPass WHERE u.id = :id")
    public void updateUserPass(@Param(value = "id")Long id,
                               @Param(value = "newPass") String newPass);

    @Modifying
    @Transactional
    @Query(value = "UPDATE User u SET u.phone = :newPhone WHERE u.id = :id")
    public void updateUserPhone(@Param(value = "id") Long id,
                                @Param(value = "newPhone")String newPhone);
     */
}
