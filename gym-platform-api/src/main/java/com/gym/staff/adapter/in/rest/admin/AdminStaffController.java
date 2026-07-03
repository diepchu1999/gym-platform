package com.gym.staff.adapter.in.rest.admin;

import com.gym.shared.api.ApiResponse;
import com.gym.shared.api.PageResponse;
import com.gym.staff.adapter.in.rest.admin.request.AssignBranchRoleRequest;
import com.gym.staff.adapter.in.rest.admin.request.CreateStaffRequest;
import com.gym.staff.adapter.in.rest.admin.request.LinkStaffUserAccountRequest;
import com.gym.staff.adapter.in.rest.admin.response.StaffDetailResponse;
import com.gym.staff.adapter.in.rest.admin.response.StaffListItemResponse;
import com.gym.staff.application.command.AssignBranchRoleCommand;
import com.gym.staff.application.command.CreateStaffCommand;
import com.gym.staff.application.command.LinkStaffUserAccountCommand;
import com.gym.staff.application.port.in.AssignBranchRoleUseCase;
import com.gym.staff.application.port.in.CreateStaffUseCase;
import com.gym.staff.application.port.in.GetStaffUseCase;
import com.gym.staff.application.port.in.LinkStaffUserAccountUseCase;
import com.gym.staff.application.port.in.SearchStaffUseCase;
import com.gym.staff.application.query.SearchStaffQuery;
import com.gym.security.api.BranchAuthorizationService;
import com.gym.security.api.SecurityPermission;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/staff")
class AdminStaffController {
    private final SearchStaffUseCase searchStaff;
    private final GetStaffUseCase getStaff;
    private final CreateStaffUseCase createStaff;
    private final AssignBranchRoleUseCase assignBranchRole;
    private final LinkStaffUserAccountUseCase linkStaffUserAccount;
    private final BranchAuthorizationService authorizationService;

    AdminStaffController(
            SearchStaffUseCase searchStaff,
            GetStaffUseCase getStaff,
            CreateStaffUseCase createStaff,
            AssignBranchRoleUseCase assignBranchRole,
            LinkStaffUserAccountUseCase linkStaffUserAccount,
            BranchAuthorizationService authorizationService
    ) {
        this.searchStaff = searchStaff;
        this.getStaff = getStaff;
        this.createStaff = createStaff;
        this.assignBranchRole = assignBranchRole;
        this.linkStaffUserAccount = linkStaffUserAccount;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    ApiResponse<PageResponse<StaffListItemResponse>> search(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        PageResponse<StaffListItemResponse> result = searchStaff
                .handle(SearchStaffQuery.from(status, keyword, page, size))
                .map(StaffListItemResponse::fromDomain);
        return ApiResponse.success("STAFF_SEARCHED", "Staff fetched", result);
    }

    @GetMapping("/{employeeCode}")
    ApiResponse<StaffDetailResponse> get(@PathVariable String employeeCode) {
        return ApiResponse.success(
                "STAFF_FETCHED",
                "Staff fetched",
                StaffDetailResponse.fromDomain(getStaff.handle(employeeCode))
        );
    }

    @PostMapping
    ApiResponse<StaffDetailResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreateStaffRequest request
    ) {
        CreateStaffCommand command = CreateStaffCommand.from(
                request.employeeCode(),
                request.fullName(),
                request.phone(),
                request.email()
        );
        authorizationService.requireGlobalPermission(jwt.getSubject(), SecurityPermission.STAFF_MANAGE);
        return ApiResponse.success(
                "STAFF_CREATED",
                "Staff created",
                StaffDetailResponse.fromDomain(createStaff.handle(command))
        );
    }

    @PostMapping("/{employeeCode}/assignments")
    ApiResponse<StaffDetailResponse> assign(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String employeeCode,
            @RequestBody AssignBranchRoleRequest request
    ) {
        AssignBranchRoleCommand command = AssignBranchRoleCommand.from(
                request.branchCode(),
                request.roleCode()
        );
        if (command.branchCode() == null) {
            authorizationService.requireGlobalPermission(jwt.getSubject(), SecurityPermission.RBAC_MANAGE);
        } else {
            authorizationService.requireBranchPermission(
                    jwt.getSubject(),
                    command.branchCode(),
                    SecurityPermission.RBAC_MANAGE
            );
        }
        return ApiResponse.success(
                "STAFF_ASSIGNMENT_CREATED",
                "Staff assignment created",
                StaffDetailResponse.fromDomain(assignBranchRole.handle(employeeCode, command))
        );
    }

    @PutMapping("/{employeeCode}/user-account")
    ApiResponse<StaffDetailResponse> linkUserAccount(
            @PathVariable String employeeCode,
            @RequestBody LinkStaffUserAccountRequest request
    ) {
        LinkStaffUserAccountCommand command = LinkStaffUserAccountCommand.from(
                request.keycloakUserId(),
                request.username(),
                request.email()
        );
        return ApiResponse.success(
                "STAFF_USER_ACCOUNT_LINKED",
                "Staff user account linked",
                StaffDetailResponse.fromDomain(linkStaffUserAccount.handle(employeeCode, command))
        );
    }
}
