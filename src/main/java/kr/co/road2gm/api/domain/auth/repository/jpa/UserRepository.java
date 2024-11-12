package kr.co.road2gm.api.domain.auth.repository.jpa;

import kr.co.road2gm.api.domain.auth.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryQuery {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN FETCH u.socialAccounts " +
            "WHERE u.email = :email")
    Optional<User> findByEmailWithSocialAccounts(@Param("email") String email);
}
