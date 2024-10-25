package kr.co.road2gm.api.domain.book.controller;

import kr.co.road2gm.api.domain.book.dto.BookResponse;
import kr.co.road2gm.api.domain.book.service.BookService;
import kr.co.road2gm.api.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    // create{Resource}
    // get{Resource}
    // update{Resource}
    // delete{Resource}

    @GetMapping("/books")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.findAllBooks();
        return ResponseEntity.ok(ApiResponse.success(books));
    }
}
