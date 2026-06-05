package com.TMDB.ProxyServer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/tmdb")
@CrossOrigin(origins = "*")
public class TmdbController {

    @Value("${tmdb.base.url}")
    private String baseUrl;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Proxy is working!");
    }

    @GetMapping("/**")
    public ResponseEntity<String> proxyGetRequest(
            HttpServletRequest request,
            @RequestParam(required = false) Map<String, String> allParams) {

        try {
            // Extract endpoint from URL
            String fullPath = request.getRequestURI();
            String endpoint = fullPath.replace("/api/tmdb/", "");

            StringBuilder urlBuilder = new StringBuilder(baseUrl + "/" + endpoint);

            if (allParams != null && !allParams.isEmpty()) {
                urlBuilder.append("?");
                allParams.forEach((key, value) ->
                        urlBuilder.append(key).append("=").append(value).append("&")
                );
                urlBuilder.setLength(urlBuilder.length() - 1);
            }

            String url = urlBuilder.toString();
            System.out.println("Proxying: " + url);

            String response = restTemplate.getForObject(url, String.class);

            return ResponseEntity.ok()
                    .header("Content-Type", "application/json")
                    .body(response);

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }
}