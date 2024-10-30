package kr.co.road2gm.api.domain.book.controller;

import kr.co.road2gm.api.domain.book.dto.BookResponse;
import kr.co.road2gm.api.domain.book.service.BookService;
import kr.co.road2gm.api.global.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Slf4j
public class BookController {
    private final BookService bookService;

    // create{Resource}
    // update{Resource}
    // delete{Resource}

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<BookResponse>>> getAllBooks() {
        List<BookResponse> books = bookService.findAllBooks();

        if (books.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(ApiResponse.success(books));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookResponse>> getBook(@PathVariable Long id) {
        BookResponse response = bookService.findMember(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // 생성 성공 응답
    // return ResponseEntity.created(uri).body(ApiResponse.success(data, "Created successfully"));

    // 에러 응답 (직접 생성 시)
    // return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse.of("Invalid input", 400, "BAD_REQUEST"));
}
