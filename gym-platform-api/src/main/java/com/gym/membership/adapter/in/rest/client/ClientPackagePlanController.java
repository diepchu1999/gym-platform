package com.gym.membership.adapter.in.rest.client;

import com.gym.membership.adapter.in.rest.client.response.ClientPackagePlanResponse;
import com.gym.membership.application.port.in.ListActivePackagePlansUseCase;
import com.gym.shared.api.ApiResponse;
import com.gym.shared.api.ListResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/client/package-plans")
class ClientPackagePlanController {
    private final ListActivePackagePlansUseCase listActivePackagePlans;

    ClientPackagePlanController(ListActivePackagePlansUseCase listActivePackagePlans) {
        this.listActivePackagePlans = listActivePackagePlans;
    }

    @GetMapping
    ApiResponse<ListResponse<ClientPackagePlanResponse>> listActive() {
        ListResponse<ClientPackagePlanResponse> result = ListResponse.of(
                listActivePackagePlans.handle().items().stream()
                        .map(ClientPackagePlanResponse::fromDomain)
                        .toList()
        );
        return ApiResponse.success("CLIENT_PACKAGE_PLANS_FETCHED", "Active package plans fetched", result);
    }
}
