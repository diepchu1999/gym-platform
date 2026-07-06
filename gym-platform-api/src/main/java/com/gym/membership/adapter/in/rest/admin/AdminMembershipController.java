package com.gym.membership.adapter.in.rest.admin;

import com.gym.membership.adapter.in.rest.admin.request.CreateMembershipRequest;
import com.gym.membership.adapter.in.rest.admin.request.UpdateMembershipStatusRequest;
import com.gym.membership.adapter.in.rest.admin.response.MembershipDetailResponse;
import com.gym.membership.adapter.in.rest.admin.response.MembershipListItemResponse;
import com.gym.membership.application.command.CreateMembershipCommand;
import com.gym.membership.application.command.UpdateMembershipStatusCommand;
import com.gym.membership.application.port.in.CreateMembershipUseCase;
import com.gym.membership.application.port.in.GetMembershipUseCase;
import com.gym.membership.application.port.in.ListMemberMembershipsUseCase;
import com.gym.membership.application.port.in.UpdateMembershipStatusUseCase;
import com.gym.membership.application.query.ListMemberMembershipsQuery;
import com.gym.membership.application.view.MembershipDetail;
import com.gym.security.api.BranchAuthorizationService;
import com.gym.security.api.SecurityPermission;
import com.gym.shared.api.ApiResponse;
import com.gym.shared.api.ListResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
class AdminMembershipController {
    private final CreateMembershipUseCase createMembership;
    private final GetMembershipUseCase getMembership;
    private final ListMemberMembershipsUseCase listMemberMemberships;
    private final UpdateMembershipStatusUseCase updateMembershipStatus;
    private final BranchAuthorizationService authorizationService;

    AdminMembershipController(
            CreateMembershipUseCase createMembership,
            GetMembershipUseCase getMembership,
            ListMemberMembershipsUseCase listMemberMemberships,
            UpdateMembershipStatusUseCase updateMembershipStatus,
            BranchAuthorizationService authorizationService
    ) {
        this.createMembership = createMembership;
        this.getMembership = getMembership;
        this.listMemberMemberships = listMemberMemberships;
        this.updateMembershipStatus = updateMembershipStatus;
        this.authorizationService = authorizationService;
    }

    @PostMapping("/memberships")
    ApiResponse<MembershipDetailResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateMembershipRequest request
    ) {
        CreateMembershipCommand command = CreateMembershipCommand.from(
                request.memberCode(),
                request.packagePlanCode(),
                request.saleBranchCode(),
                request.effectiveFrom()
        );
        authorizationService.requireBranchPermission(
                jwt.getSubject(),
                command.saleBranchCode(),
                SecurityPermission.PACKAGE_SELL
        );

        return ApiResponse.success(
                "MEMBERSHIP_CREATED",
                "Membership created",
                MembershipDetailResponse.fromDomain(createMembership.handle(command))
        );
    }

    @GetMapping("/memberships/{code}")
    ApiResponse<MembershipDetailResponse> get(@PathVariable String code) {
        return ApiResponse.success(
                "MEMBERSHIP_FETCHED",
                "Membership fetched",
                MembershipDetailResponse.fromDomain(getMembership.handle(code))
        );
    }

    @GetMapping("/members/{memberCode}/memberships")
    ApiResponse<ListResponse<MembershipListItemResponse>> listByMember(@PathVariable String memberCode) {
        ListResponse<MembershipListItemResponse> result = ListResponse.of(
                listMemberMemberships.handle(ListMemberMembershipsQuery.from(memberCode)).items().stream()
                        .map(MembershipListItemResponse::fromDomain)
                        .toList()
        );
        return ApiResponse.success("MEMBER_MEMBERSHIPS_FETCHED", "Member memberships fetched", result);
    }

    @PatchMapping("/memberships/{code}/status")
    ApiResponse<MembershipDetailResponse> updateStatus(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String code,
            @RequestBody UpdateMembershipStatusRequest request
    ) {
        MembershipDetail current = getMembership.handle(code);
        authorizationService.requireBranchPermission(
                jwt.getSubject(),
                current.saleBranchCode(),
                SecurityPermission.PACKAGE_SELL
        );

        UpdateMembershipStatusCommand command = UpdateMembershipStatusCommand.from(request.status());
        return ApiResponse.success(
                "MEMBERSHIP_STATUS_UPDATED",
                "Membership status updated",
                MembershipDetailResponse.fromDomain(updateMembershipStatus.handle(code, command))
        );
    }
}
