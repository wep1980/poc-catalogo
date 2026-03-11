package br.com.wepdev.dscatalog.controller;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    private final Instant startTime = Instant.now();

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                    .withZone(ZoneId.of("America/Sao_Paulo"));

    @GetMapping("/system/info")
    public ResponseEntity<?> info() {

        try {
            Map<String, Object> response = new HashMap<>();

            String version = System.getenv("APP_VERSION");
            String buildTime = System.getenv("BUILD_TIME");

            response.put("application", "dscatalog");
            response.put("version", version != null ? version : "unknown");

            response.put("buildTime",
                    buildTime != null
                            ? FORMATTER.format(Instant.parse(buildTime))
                            : "unknown");

            response.put("containerStartTime",
                    FORMATTER.format(startTime));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();

            error.put("error", "SYSTEM_INFO_ERROR");
            error.put("message", e.getMessage());
            error.put("timestamp",
                    FORMATTER.format(Instant.now()));

            return ResponseEntity.internalServerError().body(error);
        }
    }
}