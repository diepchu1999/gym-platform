package com.gym.shared.api;

public record PageParams(int page, int size) {
    public static PageParams normalize(Integer page, Integer size, int defaultSize, int maxSize) {
        int safePage = (page == null || page < 1) ? 1 : page;
        int safeSize = (size == null || size < 1) ? defaultSize : Math.min(size, maxSize);
        return new PageParams(safePage, safeSize);
    }

    public int pageIndex() {
        return page - 1;
    }
}
