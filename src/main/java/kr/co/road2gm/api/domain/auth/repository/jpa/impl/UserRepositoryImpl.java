package kr.co.road2gm.api.domain.auth.repository.jpa.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.road2gm.api.domain.auth.domain.QRole;
import kr.co.road2gm.api.domain.auth.domain.QUser;
import kr.co.road2gm.api.domain.auth.domain.QUserRole;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.domain.auth.repository.jpa.UserRepositoryQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class UserRepositoryImpl implements UserRepositoryQuery {
    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<User> findByEmailWithRoles(String email) {
        QUser user = QUser.user;
        QUserRole userRole = QUserRole.userRole;
        QRole role = QRole.role;

        User result = queryFactory
                .selectFrom(user)
                .leftJoin(user.userRoles, userRole).fetchJoin()
                .leftJoin(userRole.role, role).fetchJoin()
                .where(user.email.eq(email))
                .fetchOne(); // 결과가 없으면 null, 2건 이상이면 NonUniqueResultException

        return Optional.ofNullable(result);
    }
}
