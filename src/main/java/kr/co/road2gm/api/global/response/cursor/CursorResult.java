package kr.co.road2gm.api.global.response.cursor;


import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Getter
public class CursorResult<T> {
    private final List<T> items;
    private final long nextCursor;

    public CursorResult(List<T> items, long nextCursor) {
        this.items = items;
        this.nextCursor = nextCursor;
    }

    // 빈 결과를 반환하는 정적 팩토리 메서드
    public static <T> CursorResult<T> empty() {
        return new CursorResult<>(Collections.emptyList(), -1L);
    }

    // 커서 결과를 생성하는 정적 팩토리 메서드
    public static <T> CursorResult<T> of(List<T> items, long size, Function<T, Long> selector) {
        // 다음 페이지 존재 여부를 확인하기 위해 size + 1개를 조회했다고 가정
        long nextCursor = items.size() > size ? selector.apply(items.get(items.size() - 1)) : -1;
        int sliceSize = (int) Math.min(size, items.size());

        return new CursorResult<>(
                items.subList(0, sliceSize),
                nextCursor
        );
    }
}
