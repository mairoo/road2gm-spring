package kr.co.road2gm.api.domain.book.service;

import kr.co.road2gm.api.domain.auth.repository.jpa.BookRepository;
import kr.co.road2gm.api.domain.book.domain.Book;
import kr.co.road2gm.api.domain.book.dto.BookResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    //save/register (생성)
    //find (조회)
    //update (수정)
    //delete/remove (삭제)

    public List<BookResponse> findAllBooks() {
        return bookRepository.findAll().stream().map(BookResponse::new).toList();
    }

    public BookResponse findMember(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException());
        return new BookResponse(book);
    }
}
