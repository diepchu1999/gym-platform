package com.gym.branch.adapter.in.rest.admin.response;

import com.gym.branch.application.view.BranchOption;

public record BranchOptionResponse(String code, String name) {
    public static BranchOptionResponse fromDomain(BranchOption branch) {
        return new BranchOptionResponse(branch.code(), branch.name());
    }
}
