package com.gym.security.adapter.in.rest;

import com.gym.shared.api.ApiResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
class MeController {
    @GetMapping("/api/v1/me")
    ApiResponse<MeResponse> me(@AuthenticationPrincipal Jwt jwt, Authentication authentication) {
        return ApiResponse.success(
                "CURRENT_USER_FETCHED",
                "Current user fetched",
                MeResponse.from(jwt, authentication)
        );
    }

    private record MeResponse(
            String sub,
            String preferredUsername,
            List<String> realmRoles,
            List<String> authorities
    ) {
        static MeResponse from(Jwt jwt, Authentication authentication) {
            return new MeResponse(
                    jwt.getSubject(),
                    jwt.getClaimAsString("preferred_username"),
                    realmRoles(jwt),
                    authorities(authentication)
            );
        }

        private static List<String> realmRoles(Jwt jwt) {
            Object realmAccessClaim = jwt.getClaim("realm_access");
            if (!(realmAccessClaim instanceof Map<?, ?> realmAccess)) {
                return List.of();
            }

            Object rolesClaim = realmAccess.get("roles");
            if (!(rolesClaim instanceof Collection<?> roles)) {
                return List.of();
            }

            return roles.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .sorted()
                    .toList();
        }

        private static List<String> authorities(Authentication authentication) {
            return authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .sorted()
                    .toList();
        }
    }
}
