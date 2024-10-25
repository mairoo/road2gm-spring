package kr.co.road2gm.api.domain.auth.repository.jpa;

import kr.co.road2gm.api.domain.book.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // CREATE / UPDATE - save() 메서드 사용

    // READ

    // DELETE

    // Custom Queries
}
