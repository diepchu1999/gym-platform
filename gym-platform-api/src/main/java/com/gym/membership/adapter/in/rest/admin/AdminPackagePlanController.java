package com.gym.membership.adapter.in.rest.admin;

import com.gym.membership.adapter.in.rest.admin.request.CreatePackagePlanRequest;
import com.gym.membership.adapter.in.rest.admin.request.SetPackagePlanActiveRequest;
import com.gym.membership.adapter.in.rest.admin.request.UpdatePackagePlanRequest;
import com.gym.membership.adapter.in.rest.admin.response.PackagePlanDetailResponse;
import com.gym.membership.adapter.in.rest.admin.response.PackagePlanListItemResponse;
import com.gym.membership.application.command.CreatePackagePlanCommand;
import com.gym.membership.application.command.SetPackagePlanActiveCommand;
import com.gym.membership.application.command.UpdatePackagePlanCommand;
import com.gym.membership.application.port.in.CreatePackagePlanUseCase;
import com.gym.membership.application.port.in.GetPackagePlanUseCase;
import com.gym.membership.application.port.in.SearchPackagePlansUseCase;
import com.gym.membership.application.port.in.SetPackagePlanActiveUseCase;
import com.gym.membership.application.port.in.UpdatePackagePlanUseCase;
import com.gym.membership.application.query.SearchPackagePlansQuery;
import com.gym.security.api.BranchAuthorizationService;
import com.gym.security.api.SecurityPermission;
import com.gym.shared.api.ApiResponse;
import com.gym.shared.api.PageResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/package-plans")
class AdminPackagePlanController {
    private final CreatePackagePlanUseCase createPackagePlan;
    private final SearchPackagePlansUseCase searchPackagePlans;
    private final GetPackagePlanUseCase getPackagePlan;
    private final UpdatePackagePlanUseCase updatePackagePlan;
    private final SetPackagePlanActiveUseCase setPackagePlanActive;
    private final BranchAuthorizationService authorizationService;

    AdminPackagePlanController(
            CreatePackagePlanUseCase createPackagePlan,
            SearchPackagePlansUseCase searchPackagePlans,
            GetPackagePlanUseCase getPackagePlan,
            UpdatePackagePlanUseCase updatePackagePlan,
            SetPackagePlanActiveUseCase setPackagePlanActive,
            BranchAuthorizationService authorizationService
    ) {
        this.createPackagePlan = createPackagePlan;
        this.searchPackagePlans = searchPackagePlans;
        this.getPackagePlan = getPackagePlan;
        this.updatePackagePlan = updatePackagePlan;
        this.setPackagePlanActive = setPackagePlanActive;
        this.authorizationService = authorizationService;
    }

    @GetMapping
    ApiResponse<PageResponse<PackagePlanListItemResponse>> search(
            @RequestParam(required = false) String packageType,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        PageResponse<PackagePlanListItemResponse> result = searchPackagePlans
                .handle(SearchPackagePlansQuery.from(packageType, isActive, keyword, page, size))
                .map(PackagePlanListItemResponse::fromDomain);
        return ApiResponse.success("PACKAGE_PLANS_SEARCHED", "Package plans fetched", result);
    }

    @GetMapping("/{code}")
    ApiResponse<PackagePlanDetailResponse> get(@PathVariable String code) {
        return ApiResponse.success(
                "PACKAGE_PLAN_FETCHED",
                "Package plan fetched",
                PackagePlanDetailResponse.fromDomain(getPackagePlan.handle(code))
        );
    }

    @PostMapping
    ApiResponse<PackagePlanDetailResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody CreatePackagePlanRequest request
    ) {
        authorizationService.requireGlobalPermission(jwt.getSubject(), SecurityPermission.PACKAGE_MANAGE);
        CreatePackagePlanCommand command = CreatePackagePlanCommand.from(
                request.code(),
                request.name(),
                request.packageType(),
                request.durationDays(),
                request.price(),
                request.currency(),
                request.vip(),
                request.studentOnly(),
                request.totalSessions(),
                request.dailyCheckinLimit(),
                request.privateRoomMinutesPerMonth(),
                request.massageFreePerWeek(),
                request.installmentAllowed(),
                request.active()
        );
        return ApiResponse.success(
                "PACKAGE_PLAN_CREATED",
                "Package plan created",
                PackagePlanDetailResponse.fromDomain(createPackagePlan.handle(command))
        );
    }

    @PatchMapping("/{code}")
    ApiResponse<PackagePlanDetailResponse> update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String code,
            @RequestBody UpdatePackagePlanRequest request
    ) {
        authorizationService.requireGlobalPermission(jwt.getSubject(), SecurityPermission.PACKAGE_MANAGE);
        UpdatePackagePlanCommand command = UpdatePackagePlanCommand.from(
                request.name(),
                request.packageType(),
                request.durationDays(),
                request.price(),
                request.currency(),
                request.vip(),
                request.studentOnly(),
                request.totalSessions(),
                request.dailyCheckinLimit(),
                request.privateRoomMinutesPerMonth(),
                request.massageFreePerWeek(),
                request.installmentAllowed()
        );
        return ApiResponse.success(
                "PACKAGE_PLAN_UPDATED",
                "Package plan updated",
                PackagePlanDetailResponse.fromDomain(updatePackagePlan.handle(code, command))
        );
    }

    @PatchMapping("/{code}/active")
    ApiResponse<PackagePlanDetailResponse> setActive(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable String code,
            @RequestBody SetPackagePlanActiveRequest request
    ) {
        authorizationService.requireGlobalPermission(jwt.getSubject(), SecurityPermission.PACKAGE_MANAGE);
        SetPackagePlanActiveCommand command = SetPackagePlanActiveCommand.from(request.active());
        return ApiResponse.success(
                "PACKAGE_PLAN_ACTIVE_CHANGED",
                "Package plan active flag changed",
                PackagePlanDetailResponse.fromDomain(setPackagePlanActive.handle(code, command))
        );
    }
}
