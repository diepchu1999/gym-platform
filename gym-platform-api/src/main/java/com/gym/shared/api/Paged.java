package com.gym.shared.api;

public interface Paged {

    int page();

    int size();

    default int pageIndex() {
        return page() -1;
    }
}
