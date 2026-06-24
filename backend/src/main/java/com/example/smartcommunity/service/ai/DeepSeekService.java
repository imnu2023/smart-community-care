package com.example.smartcommunity.service.ai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * DeepSeek AI streaming service.
 * Calls DeepSeek Chat Completion API with streaming, delivers tokens via callback.
 * Pure HttpURLConnection — no WebFlux, no extra dependencies.
 */
@Service
public class DeepSeekService {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekService.class);

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.model:deepseek-chat}")
    private String model;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    public String getApiKeyPrefix() {
        return apiKey != null && apiKey.length() > 8 ? apiKey.substring(0, 8) + "..." : "NOT SET";
    }

    public String getModel() {
        return model;
    }

    /**
     * Stream a chat completion from DeepSeek, pushing each content delta to the callback.
     */
    public void streamChat(String systemPrompt, String userMessage, Consumer<String> onToken) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new RuntimeException("DeepSeek API key is not configured. Please set deepseek.api-key in application.yml");
        }
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            throw new RuntimeException("DeepSeek base URL is not configured. Please set deepseek.base-url in application.yml");
        }

        String apiUrl = baseUrl.replaceAll("/$", "") + "/v1/chat/completions";

        // Build JSON request body manually (Java 8, no JSON lib needed for this)
        String body = buildRequestBody(systemPrompt, userMessage);

        log.info("Calling DeepSeek API: url={}, model={}, keyPrefix={}", apiUrl, model, getApiKeyPrefix());

        HttpURLConnection conn = (HttpURLConnection) new URL(apiUrl).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "text/event-stream");
        conn.setDoOutput(true);
        conn.setConnectTimeout(20000);
        conn.setReadTimeout(120000);

        // Write request body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }

        int status = conn.getResponseCode();
        if (status != 200) {
            StringBuilder err = new StringBuilder();
            if (conn.getErrorStream() != null) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String l;
                    while ((l = br.readLine()) != null) err.append(l);
                }
            }
            log.error("DeepSeek API error: status={}, body={}", status, err.toString());
            throw new RuntimeException("DeepSeek API error " + status + ": " + err.toString());
        }

        // Read SSE stream
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("data: ") && !line.contains("[DONE]")) {
                    String token = extractContent(line);
                    if (!token.isEmpty()) {
                        onToken.accept(token);
                    }
                }
            }
        }
    }

    private String buildRequestBody(String systemPrompt, String userMessage) {
        // Escape JSON strings manually
        String sysEscaped = escapeJson(systemPrompt);
        String userEscaped = escapeJson(userMessage);
        String modelEscaped = escapeJson(model);

        return "{" +
            "\"model\":\"" + modelEscaped + "\"," +
            "\"stream\":true," +
            "\"messages\":[" +
                "{\"role\":\"system\",\"content\":\"" + sysEscaped + "\"}," +
                "{\"role\":\"user\",\"content\":\"" + userEscaped + "\"}" +
            "]," +
            "\"temperature\":0.7," +
            "\"max_tokens\":600" +
        "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String extractContent(String sseLine) {
        // "data: {...\"content\":\"text here\"...}"
        try {
            String json = sseLine.substring(6).trim();
            int ci = json.indexOf("\"content\"");
            if (ci < 0) return "";
            // Find the colon after "content"
            int colon = json.indexOf(":", ci);
            if (colon < 0) return "";
            // Find the opening quote of the value
            int start = json.indexOf("\"", colon + 1);
            if (start < 0 || start + 1 >= json.length()) return "";
            // Check if value is null
            if (json.charAt(start + 1) == 'n') return "";
            // Find the closing quote (handling escaped quotes inside)
            StringBuilder sb = new StringBuilder();
            for (int i = start + 1; i < json.length(); i++) {
                char c = json.charAt(i);
                if (c == '\\' && i + 1 < json.length()) {
                    char next = json.charAt(i + 1);
                    if (next == 'n') { sb.append('\n'); i++; }
                    else if (next == '"') { sb.append('"'); i++; }
                    else if (next == '\\') { sb.append('\\'); i++; }
                    else if (next == 'r') { sb.append('\r'); i++; }
                    else if (next == 't') { sb.append('\t'); i++; }
                    else { sb.append(c); }
                } else if (c == '"') {
                    break;
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            return "";
        }
    }
}
