package kr.co.road2gm.api.domain.auth.repository.jpa;

import kr.co.road2gm.api.domain.auth.domain.User;

import java.util.Optional;

public interface UserRepositoryQuery {
    Optional<User> findByEmailWithRoles(String email);
}
