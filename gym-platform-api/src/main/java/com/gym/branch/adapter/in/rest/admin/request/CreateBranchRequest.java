package com.gym.branch.adapter.in.rest.admin.request;

public record CreateBranchRequest(
        String code,
        String name,
        String address,
        String district,
        String city,
        String phone,
        Boolean open24h
) {
}
