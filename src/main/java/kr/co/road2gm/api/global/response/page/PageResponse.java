package kr.co.road2gm.api.global.response.page;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {
    private final List<T> items;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public PageResponse(List<T> items, int pageNumber, int pageSize, long totalElements) {
        this.items = items;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.hasNext = pageNumber + 1 < this.totalPages;
        this.hasPrevious = pageNumber > 0;
    }

    public static <T> PageResponse<T> of(List<T> items, int pageNumber, int pageSize, long totalElements) {
        return new PageResponse<>(items, pageNumber, pageSize, totalElements);
    }
}
