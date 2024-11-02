package kr.co.road2gm.api.domain.book.domain;

import jakarta.persistence.*;
import kr.co.road2gm.api.domain.auth.domain.User;
import kr.co.road2gm.api.global.common.BaseDateTime;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "book")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Slf4j
public class Book extends BaseDateTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "thumbnail")
    private String thumbnail;

    @ManyToOne(optional = false,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    public static BookBuilder from(String title) {
        return new BookBuilder()
                .title(title);
    }
}
