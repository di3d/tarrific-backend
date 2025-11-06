package com.tarrific.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tarrific.backend.dto.ExchangeResponse;
import io.github.cdimascio.dotenv.Dotenv;
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

@Service
public class ExchangeService {

    private final String apiKey;
    private final String providerBase;
    private final ObjectMapper mapper = new ObjectMapper();
    private final HttpClient client = HttpClient.newBuilder().build();

    public ExchangeService() {
        Dotenv dotenv = Dotenv.load();
        this.apiKey = dotenv.get("CURRENCYAPI_KEY");
        this.providerBase = dotenv.get("EXCHANGE_PROVIDER_URL", "https://api.currencyapi.com/v3");
    }

    public ExchangeResponse convert(String from, String to, BigDecimal amount)
            throws IOException, InterruptedException {

        if (from == null || to == null || amount == null)
            throw new IllegalArgumentException("Missing parameters");

        String providerUrl = String.format(
                "%s/latest?apikey=%s&base_currency=%s&currencies=%s",
                providerBase,
                URLEncoder.encode(apiKey, StandardCharsets.UTF_8),
                URLEncoder.encode(from, StandardCharsets.UTF_8),
                URLEncoder.encode(to, StandardCharsets.UTF_8)
        );

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(providerUrl))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());

        if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
            throw new RuntimeException("Provider error: " + resp.statusCode() + " - " + resp.body());
        }

        JsonNode payload = mapper.readTree(resp.body());
        if (!payload.has("data") || !payload.get("data").has(to)) {
            throw new RuntimeException("Invalid provider response: " + resp.body());
        }

        JsonNode currencyNode = payload.get("data").get(to);
        BigDecimal rate = currencyNode.get("value").decimalValue();
        BigDecimal converted = rate.multiply(amount).setScale(10, RoundingMode.HALF_UP);
        String date = payload.path("meta").path("last_updated_at").asText(OffsetDateTime.now().toString());

        System.out.printf("✅ 1 %s = %.6f %s (via CurrencyAPI)%n", from, rate, to);

        return new ExchangeResponse(converted, rate, to.toUpperCase(), date);
    }
}
