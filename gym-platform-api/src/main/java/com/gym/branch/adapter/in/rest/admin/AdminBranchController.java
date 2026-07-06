package com.gym.branch.adapter.in.rest.admin;

import com.gym.branch.adapter.in.rest.admin.request.CreateBranchRequest;
import com.gym.branch.adapter.in.rest.admin.response.BranchDetailResponse;
import com.gym.branch.adapter.in.rest.admin.response.BranchListItemResponse;
import com.gym.branch.adapter.in.rest.admin.response.BranchOptionResponse;
import com.gym.branch.application.command.CreateBranchCommand;
import com.gym.branch.application.port.in.CreateBranchUseCase;
import com.gym.branch.application.port.in.GetBranchUseCase;
import com.gym.branch.application.port.in.ListBranchesUseCase;
import com.gym.branch.application.port.in.SearchBranchesUseCase;
import com.gym.branch.application.query.SearchBranchesQuery;
import com.gym.shared.api.ApiResponse;
import com.gym.shared.api.ListResponse;
import com.gym.shared.api.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/branches")
class AdminBranchController {
    private final SearchBranchesUseCase searchBranches;
    private final GetBranchUseCase getBranch;
    private final ListBranchesUseCase listBranches;
    private final CreateBranchUseCase createBranch;

    AdminBranchController(
            SearchBranchesUseCase searchBranches,
            GetBranchUseCase getBranch,
            ListBranchesUseCase listBranches,
            CreateBranchUseCase createBranch
    ) {
        this.searchBranches = searchBranches;
        this.getBranch = getBranch;
        this.listBranches = listBranches;
        this.createBranch = createBranch;
    }

    @GetMapping
    ApiResponse<PageResponse<BranchListItemResponse>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        PageResponse<BranchListItemResponse> result = searchBranches
                .handle(SearchBranchesQuery.from(status, keyword, page, size))
                .map(BranchListItemResponse::fromDomain);
        return ApiResponse.success("BRANCH_SEARCHED", "Branches fetched", result);
    }

    @GetMapping("/lookup")
    ApiResponse<ListResponse<BranchOptionResponse>> lookup() {
        ListResponse<BranchOptionResponse> result = ListResponse.of(
                listBranches.handle().items().stream()
                        .map(BranchOptionResponse::fromDomain)
                        .toList()
        );
        return ApiResponse.success("BRANCH_LOOKUP_FETCHED", "Active branches fetched", result);
    }

    @GetMapping("/{code}")
    ApiResponse<BranchDetailResponse> get(@PathVariable String code) {
        return ApiResponse.success(
                "BRANCH_FETCHED",
                "Branch fetched",
                BranchDetailResponse.fromDomain(getBranch.handle(code))
        );
    }

    @PostMapping
    ApiResponse<BranchDetailResponse> create(@RequestBody CreateBranchRequest request) {
        CreateBranchCommand command = CreateBranchCommand.from(
                request.code(),
                request.name(),
                request.address(),
                request.district(),
                request.city(),
                request.phone(),
                request.open24h()
        );
        return ApiResponse.success(
                "BRANCH_CREATED",
                "Branch created",
                BranchDetailResponse.fromDomain(createBranch.handle(command))
        );
    }
}
