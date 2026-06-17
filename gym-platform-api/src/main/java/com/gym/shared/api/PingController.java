package com.gym.shared.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Endpoint kiểm tra walking skeleton: web layer + ApiResponse envelope hoạt động.
 */
@RestController
public class PingController {

    @GetMapping("/api/ping")
    public ApiResponse<Map<String, Object>> ping() {
        return ApiResponse.ok(Map.of(
                "status", "ok",
                "service", "gym-platform-api",
                "time", Instant.now().toString()
        ));
    }
}
