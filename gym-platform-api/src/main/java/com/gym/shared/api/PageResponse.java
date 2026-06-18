package com.gym.shared.api;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> items,
        long total,
        int page,
        int size,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {
    public static <T> PageResponse<T> of(
            List<T> items, long total, int page, int size,
            int totalPages, boolean hasNext, boolean hasPrevious
    ) {
        return new PageResponse<>(items, total, page, size, totalPages, hasNext, hasPrevious);
    }

    public static <T> PageResponse<T> ofPageIndex(List<T> items, long total, int pageIndex, int size) {
        int page = pageIndex + 1;
        int totalPages = total == 0 ? 1 : (int) Math.ceil((double) total / size);
        return new PageResponse<>(items, total, page, size, totalPages, page < totalPages, page > 1);
    }

    public <U> PageResponse<U> map(Function<? super T, ? extends U> mapper) {
        return new PageResponse<>(
                items.stream().<U>map(mapper).toList(),
                total, page, size, totalPages, hasNext, hasPrevious
        );
    }
}
