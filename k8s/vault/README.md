# Vault

Не используется в проекте, но при желании можно развернуть применив манифест:

```bash
kubectl apply -f vault-deployment.yaml
```

Для применения секретов можно запустить команду:

```bash
cd terraform
terraform init
terraform apply -auto-approve
```
