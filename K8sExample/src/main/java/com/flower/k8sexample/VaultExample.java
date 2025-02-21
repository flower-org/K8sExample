package com.flower.k8sexample;

import java.nio.file.Files;
import java.nio.file.Paths;

public class VaultExample {
    private static final String KUBERNETES_JWT_TOKEN_FILE = "/var/run/secrets/kubernetes.io/serviceaccount/token";
    private static final String VAULT_URL = "http://vault.vault.svc.cluster.local";
    private static final int VAULT_PORT = 8200;

    public static void main(String[] args) {
        try {
            System.out.println("1. Authenticating at vault");
            String jwt = Files.readString(Paths.get(KUBERNETES_JWT_TOKEN_FILE));
            String vaultToken = VaultRestClient.kubernetesAuth(VAULT_URL, VAULT_PORT, jwt);
            System.out.println("Successfully authenticated");

            System.out.println("1. Creating a wrapped token");
            String podName = System.getenv("HOSTNAME");
            String jsonPayload = "{ \"myToken\" : \"FooBar\", \"podName\" : \"" + podName + "\" }";
            String wrappedToken = VaultRestClient.createWrappedToken(VAULT_URL, VAULT_PORT, vaultToken, jsonPayload);
            System.out.println("Wrapped token created: " + wrappedToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
