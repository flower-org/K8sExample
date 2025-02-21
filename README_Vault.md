Vault - service:

1. Enable the Kubernetes auth method:
```bash
vault auth enable kubernetes
```

2. Use the /config endpoint to configure Vault to talk to Kubernetes. 
Use kubectl cluster-info to validate the Kubernetes host address and TCP port. 
For the list of available configuration options, please see the API documentation.
The token and CA certificate are usually available within the Kubernetes pod via environment variables or mounted files.
The token is available at `/var/run/secrets/kubernetes.io/serviceaccount/token`.
The CA certificate is available at `/var/run/secrets/kubernetes.io/serviceaccount/ca.crt`.
```bash
vault write auth/kubernetes/config \
 token_reviewer_jwt="$(cat /var/run/secrets/kubernetes.io/serviceaccount/token)" \
 kubernetes_host=https://${KUBERNETES_PORT_443_TCP_ADDR}:443 \
 kubernetes_ca_cert=@/var/run/secrets/kubernetes.io/serviceaccount/ca.crt
```
3. 
4. Add create-only policy
```bash
vault policy write create-only-policy - <<EOF
path "secret/my-service" {
  capabilities = ["create"]
}
EOF
```

4. Create a named role:
Upon K8s auth, pods get policy tied to their service account?
```bash
vault write auth/kubernetes/role/vault-auth \
    bound_service_account_names=vault-auth \
    bound_service_account_namespaces=default \
    policies=create-only-policy \
    ttl=10m
```

5. Test role and auth
```bash
vault read auth/kubernetes/role/vault-auth
```

```bash
vault login -method=kubernetes role=vault-auth
```

---------------------------------------------

Vault - user

```
path "secret/my-service/*" {
  capabilities = ["read", "delete"]
}
```

```bash
vault policy write read-delete-policy /path/to/read-delete-policy.hcl
```

```bash
vault write auth/kubernetes/role/read-delete-role \
    bound_service_account_names=<your-service-account> \
    bound_service_account_namespaces=<your-namespace> \
    policies=read-delete-policy \
    ttl=10m
```
