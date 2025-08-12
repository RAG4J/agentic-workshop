package org.rag4j.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.springframework.web.util.HtmlUtils.htmlEscape;

@Controller
public class TokenController {
    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final String openAIProxyUrl;
    private final Optional<String> openAIProxyToken;

    public TokenController(
            @Value("${openai.proxy.url}") String openAIProxyUrl,
            @Value("${openai.proxy.token:#{null}}") Optional<String> openAIProxyToken
    ) {
        this.openAIProxyUrl = openAIProxyUrl;
        this.openAIProxyToken = openAIProxyToken;
    }

    @GetMapping("/token")
    public String homePage(Model model) {
        if (openAIProxyToken.isEmpty()) {
            model.addAttribute("error", "OpenAI proxy token is not set. Please request a token.");
        }
        return "token";
    }

    @PostMapping("/token")
    public String handleFetchToken(
            @Validated @RequestParam("userId") String userId, Model model) {
        if (userId == null || userId.trim().isEmpty()) {
            model.addAttribute("confirmation", null);
            model.addAttribute("error", "You need to provide a username.");
            return "token";
        }
        // Sanitize message to prevent XSS
        String sanitizedUserId = htmlEscape(userId);
        logger.info("Received userId for token: {}", sanitizedUserId);

        // Fetch the token for the provided userId if the token is not already stored in the application properties file
        String token = this.fetchTokenForUser(sanitizedUserId);
        if (token == null || token.isEmpty()) {
            model.addAttribute("confirmation", null);
            model.addAttribute("error", "Failed to obtain a token for the user. Please try again.");
            return "token";
        }
        logger.info("Token obtained for userId {}: {}", sanitizedUserId, token);

        model.addAttribute("confirmation", "Token is obtained, now copy it and add it to application.properties!");
        model.addAttribute("obtainedToken", token);
        model.addAttribute("userId", userId);
        model.addAttribute("error", null);
        return "token";
    }

    private String fetchTokenForUser(String userId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = this.openAIProxyUrl + "/token";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("name", userId);
        requestBody.put("minutes", 180);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        return response.getBody();
    }
}
