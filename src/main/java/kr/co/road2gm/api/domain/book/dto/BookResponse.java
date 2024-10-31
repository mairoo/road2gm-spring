package kr.co.road2gm.api.domain.book.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import kr.co.road2gm.api.domain.book.domain.Book;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class BookResponse {
    @JsonProperty("bookId")
    private Long id;

    @JsonProperty("title")
    private String title;

    // 객체에서 필요한 정보만 노출하는 DTO 응답 객체
    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
    }
}
