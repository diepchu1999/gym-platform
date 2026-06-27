package com.gym.member.adapter.in.rest.admin;

import com.gym.member.adapter.in.rest.admin.request.CreateMemberRequest;
import com.gym.member.adapter.in.rest.admin.response.MemberDetailResponse;
import com.gym.member.adapter.in.rest.admin.response.MemberListItemResponse;
import com.gym.member.application.command.CreateMemberCommand;
import com.gym.member.application.port.in.CreateMemberUseCase;
import com.gym.member.application.port.in.GetMemberUseCase;
import com.gym.member.application.port.in.SearchMembersUseCase;
import com.gym.member.application.query.SearchMembersQuery;
import com.gym.shared.api.ApiResponse;
import com.gym.shared.api.PageResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/members")
class AdminMemberController {
    private final CreateMemberUseCase createMemberUseCase;
    private final GetMemberUseCase getMemberUseCase;
    private final SearchMembersUseCase searchMembersUseCase;

    AdminMemberController(
            CreateMemberUseCase createMemberUseCase,
            GetMemberUseCase getMemberUseCase,
            SearchMembersUseCase searchMembersUseCase
    ) {
        this.createMemberUseCase = createMemberUseCase;
        this.getMemberUseCase = getMemberUseCase;
        this.searchMembersUseCase = searchMembersUseCase;
    }

    @GetMapping
    ApiResponse<PageResponse<MemberListItemResponse>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String branchCode,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        PageResponse<MemberListItemResponse> result = searchMembersUseCase.handle(
                        SearchMembersQuery.from(status, keyword, branchCode, page, size))
                .map(MemberListItemResponse::fromDomain);
        return ApiResponse.success("MEMBER_SEARCHED", "Members fetched", result);
    }

    @GetMapping("/{code}")
    ApiResponse<MemberDetailResponse> get(@PathVariable String code) {
        MemberDetailResponse result = MemberDetailResponse.fromDomain(getMemberUseCase.handle(code));
        return ApiResponse.success("MEMBER_FETCHED", "Member fetched", result);
    }

    @PostMapping
    ApiResponse<MemberDetailResponse> create(@RequestBody CreateMemberRequest request) {
        CreateMemberCommand command = CreateMemberCommand.from(
                request.code(),
                request.fullName(),
                request.phone(),
                request.email(),
                request.gender(),
                request.dateOfBirth(),
                request.homeBranchCode()
        );
        MemberDetailResponse response = MemberDetailResponse.fromDomain(createMemberUseCase.handle(command));
        return ApiResponse.success("MEMBER_CREATED", "Member created", response);
    }
}
