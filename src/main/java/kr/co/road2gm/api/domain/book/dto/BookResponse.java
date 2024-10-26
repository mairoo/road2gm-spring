package kr.co.road2gm.api.domain.book.dto;

import kr.co.road2gm.api.domain.book.domain.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


@Getter
@AllArgsConstructor
@Slf4j
public class BookResponse {
    private Long id;

    private String title;

    // 객체에서 필요한 정보만 노출하는 DTO 응답 객체
    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
    }
}
