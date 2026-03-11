package br.com.wepdev.dscatalog.controller;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    private final Instant startTime = Instant.now();

    @GetMapping("/system/info")
    public ResponseEntity<?> info() {

        try {
            Map<String, Object> response = new HashMap<>();

            String version = System.getenv("APP_VERSION");
            String buildTime = System.getenv("BUILD_TIME");

            response.put("application", "dscatalog");
            response.put("version", version != null ? version : "unknown");
            response.put("buildTime", buildTime != null ? buildTime : "unknown");
            response.put("containerStartTime", startTime.toString());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();

            error.put("error", "SYSTEM_INFO_ERROR");
            error.put("message", "Failed to retrieve system information");
            error.put("timestamp", Instant.now().toString());

            return ResponseEntity.internalServerError().body(error);
        }
    }
}