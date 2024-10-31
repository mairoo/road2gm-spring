package kr.co.road2gm.api.domain.book.service;

import kr.co.road2gm.api.domain.book.domain.Book;
import kr.co.road2gm.api.domain.book.dto.BookResponse;
import kr.co.road2gm.api.domain.book.repository.jpa.BookRepository;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.error.exception.ApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class BookService {
    private final BookRepository bookRepository;

    //save/register (생성)
    //find (조회)
    //update (수정)
    //delete/remove (삭제)

    public List<BookResponse>
    findAllBooks() {
        return bookRepository.findAll().stream().map(BookResponse::new).toList();
    }

    public BookResponse
    findMember(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.BOOK_NOT_FOUND));
        return new BookResponse(book);
    }
}
