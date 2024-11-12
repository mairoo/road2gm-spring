package kr.co.road2gm.api.global.response.cursor;

import lombok.Getter;

import java.util.List;

@Getter
public class CursorResponse<T> {
    private final List<T> items;
    private final long nextCursor;
    private final int size;
    private final boolean hasNext;

    public CursorResponse(CursorResult<T> cursorResult, int size) {
        this.items = cursorResult.getItems();
        this.nextCursor = cursorResult.getNextCursor();
        this.size = size;
        this.hasNext = cursorResult.getNextCursor() != -1;
    }

    public static <T> CursorResponse<T> of(CursorResult<T> cursorResult, int size) {
        return new CursorResponse<>(cursorResult, size);
    }
}
