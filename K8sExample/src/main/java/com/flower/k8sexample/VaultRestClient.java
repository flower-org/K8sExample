package com.flower.k8sexample;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VaultRestClient {
    public static String kubernetesAuth(String vaultUrl, int vaultPort, String jwt)
            throws IOException, InterruptedException {
        HttpResponse<String> authResponse;
        try (HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()) {
            String jsonPayload = "{\"jwt\": \"" + jwt + "\", \"role\": \"vault-role\"}";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(vaultUrl + ":" + vaultPort + "/v1/auth/kubernetes/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            authResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        String authResponseStr = authResponse.body();
        JSONObject responseObject = new JSONObject(authResponseStr);
        if (responseObject.has("errors")) {
            throw new RuntimeException("Authentication error: " + authResponseStr);
        } else if (responseObject.has("auth")) {
            JSONObject authObject = responseObject.getJSONObject("auth");
            return authObject.getString("client_token");
        } else {
            throw new RuntimeException("Authentication error: unknown response format " + authResponseStr);
        }
    }

    public static String createWrappedToken(String vaultUrl, int vaultPort, String vaultToken,
                                                          String jsonPayload) throws IOException, InterruptedException {
        HttpResponse<String> tokenResponse;
        try (HttpClient client = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build()) {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(vaultUrl + ":" + vaultPort + "/v1/sys/wrapping/wrap"))
                    .header("X-Vault-Token", vaultToken)
                    .header("X-Vault-Wrap-TTL", "900")//15 minutes
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            tokenResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
        }

        String tokenResponseStr = tokenResponse.body();
        JSONObject responseObject = new JSONObject(tokenResponseStr);
        if (responseObject.has("errors")) {
            throw new RuntimeException("Wrapped token creation error: " + tokenResponseStr);
        } else if (responseObject.has("wrap_info")) {
            JSONObject authObject = responseObject.getJSONObject("wrap_info");
            return authObject.getString("token");
        } else {
            throw new RuntimeException("Wrapped token creation error: unknown response format " + tokenResponseStr);
        }
    }
}
