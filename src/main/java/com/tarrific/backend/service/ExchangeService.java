package com.tarrific.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarrific.backend.dto.ExchangeResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ExchangeService {
    private final String apiKey;
    private final String providerBase;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().build();
    private static final String FALLBACK_KEY = "MY_API";

    public ExchangeService(@Value("${CURRENCYAPI_KEY:}") String envKey,
                           @Value("${EXCHANGE_PROVIDER_URL:}") String providerBase) {
        // Prefer actual environment variable. If not provided, try to load from a local .env file
        String key = envKey;
        if (key == null || key.isEmpty()) {
            try {
                Path dotEnv = Paths.get(System.getProperty("user.dir"), ".env");
                if (Files.exists(dotEnv)) {
                    List<String> lines = Files.readAllLines(dotEnv, StandardCharsets.UTF_8);
                    for (String line : lines) {
                        if (line == null) continue;
                        String trimmed = line.trim();
                        if (trimmed.startsWith("CURRENCYAPI_KEY=")) {
                            String val = trimmed.substring(trimmed.indexOf('=') + 1).trim();
                            if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                                val = val.substring(1, val.length() - 1);
                            }
                            if (!val.isEmpty()) {
                                key = val;
                                break;
                            }
                        }
                    }
                }
            } catch (Exception ignored) {
                // If reading .env fails, we'll fall back to the default key below.
            }
        }

        this.apiKey = (key != null && !key.isEmpty()) ? key : FALLBACK_KEY;
        this.providerBase = (providerBase != null && !providerBase.isEmpty()) ? providerBase : "https://api.currencyapi.com/v3";
    }

    /**
     * Convert amount from one currency to another using configured provider.
     * Returns ExchangeResponse with converted value, rate (if available), target currency and date.
     */
    public ExchangeResponse convert(String from, String to, BigDecimal amount) throws IOException, InterruptedException {
        if (from == null || to == null || amount == null) throw new IllegalArgumentException("Missing parameters");

        String providerUrl;
        if (providerBase.contains("currencyapi.com")) {
            providerUrl = String.format("%s/latest?apikey=%s&base_currency=%s&currencies=%s",
                    providerBase,
                    URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                    URLEncoder.encode(from, StandardCharsets.UTF_8),
                    URLEncoder.encode(to, StandardCharsets.UTF_8));
        } else {
            String amt = URLEncoder.encode(amount.toPlainString(), StandardCharsets.UTF_8);
            providerUrl = String.format("%s/convert?apikey=%s&from=%s&to=%s&amount=%s&value=%s",
                    providerBase,
                    URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                    URLEncoder.encode(from, StandardCharsets.UTF_8),
                    URLEncoder.encode(to, StandardCharsets.UTF_8),
                    amt,
                    amt);
        }

        HttpRequest req = HttpRequest.newBuilder().uri(URI.create(providerUrl)).GET().build();
        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Provider error: " + resp.statusCode() + " - " + resp.body());
        }

        JsonNode payload = mapper.readTree(resp.body());

        BigDecimal converted = null;
        BigDecimal rate = null;
        String date = null;

        // currencyapi.com /latest shape
        if (payload.has("data") && payload.get("data").has(to) && payload.get("data").get(to).has("value") && payload.get("data").get(to).get("value").isNumber()) {
            rate = payload.get("data").get(to).get("value").decimalValue();
            converted = rate.multiply(amount);
            date = payload.path("meta").path("last_updated_at").asText(null);
            if (date == null || date.isEmpty()) date = payload.path("data").path(to).path("date").asText(OffsetDateTime.now().toString());
        } else {
            // fallback shapes
            if (payload.has("converted") && payload.get("converted").isNumber()) converted = payload.get("converted").decimalValue();
            if (payload.has("result") && payload.get("result").isNumber()) converted = payload.get("result").decimalValue();
            if (converted == null && payload.path("data").has("result") && payload.path("data").get("result").isNumber()) converted = payload.path("data").get("result").decimalValue();

            if (payload.has("info") && payload.get("info").has("rate") && payload.get("info").get("rate").isNumber())
                rate = payload.get("info").get("rate").decimalValue();
            if (rate == null && payload.has("rate") && payload.get("rate").isNumber()) rate = payload.get("rate").decimalValue();

            date = payload.path("data").path("date").asText(null);
            if (date == null || date.isEmpty()) date = payload.path("date").asText(null);

            if ((rate == null) && converted != null && amount.compareTo(BigDecimal.ZERO) != 0) {
                rate = converted.divide(amount, 10, RoundingMode.HALF_UP);
            }
        }

        if (converted == null) {
            throw new RuntimeException("Invalid provider response: " + resp.body());
        }

        if (date == null || date.isEmpty()) date = OffsetDateTime.now().toString();

        return new ExchangeResponse(converted, rate, to.toUpperCase(), date);
    }
}
