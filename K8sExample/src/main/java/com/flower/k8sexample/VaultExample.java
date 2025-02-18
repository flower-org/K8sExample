package com.flower.k8sexample;

import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.response.AuthResponse;
import org.apache.commons.lang3.RandomStringUtils;

public class VaultExample {
    public static void main(String[] args) {
        try {
            // Vault Configuration
            VaultConfig config = new VaultConfig()
                    .address("http://your-vault-server:8200")
                    .build();

            // Initialize Vault client
            Vault vault = new Vault(config);

            // Kubernetes authentication
            String kubernetesRole = "your-kubernetes-role";
            String jwt = "your-service-account-jwt";  // Obtain the JWT token from Kubernetes

            AuthResponse kubernetesAuth = vault.auth().loginByKubernetes(kubernetesRole, jwt);
            String clientToken = kubernetesAuth.getAuthClientToken();
            System.out.println("Authenticated with Kubernetes: " + clientToken);

            // Create a wrapped single-use token with a random string
            String randomString = RandomStringUtils.randomAlphanumeric(32);  // Generate a random string
            String secretPath = "secret/random";  // Path where the random string will be stored

            // Store the random string in Vault (or any secret store you choose)
            vault.logical().write(secretPath, new java.util.HashMap<String, Object>() {{
                put("random_string", randomString);
            }});

            // Wrap the token
            // Not supported, ChatGpt hallucinated
            //String wrappedToken = vault.sys().wrap(secretPath, 3600);  // Expiry of 1 hour
            //System.out.println("Wrapped Token: " + wrappedToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
