package kr.co.road2gm.api.domain.book.controller.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class BookCreateRequest {
    @JsonProperty("title")
    @NotBlank
    @NotEmpty
    private String title;
}
