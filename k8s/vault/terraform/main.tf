terraform {
  required_providers {
    vault = {
      source  = "hashicorp/vault"
      version = "4.3.0"
    }
  }
}
# Подключение к Vault
provider "vault" {
  address = "http://localhost:31000" # URL вашего Vault
  token = "myroot"                 # Ваш root-токен или другой валидный токен
}

# Добавление секрета в Vault
resource "vault_generic_secret" "db_security_config" {
  path = "secret/db/pg/security"
  data_json = jsonencode({
    host = "security-db-service",
    port = "5432",
    user = "user",
    pass = "secret",
    name = "auth"
  })
}

resource "vault_generic_secret" "db_payment_config" {
  path = "secret/db/pg/payment"
  data_json = jsonencode({
    host = "payment-db-service",
    port = "5433",
    user = "user",
    pass = "secret",
    name = "payment"
  })
}

resource "vault_generic_secret" "db_order_config" {
  path = "secret/db/mongo/order"
  data_json = jsonencode({
    host = "order-mongo-service",
    port = "27017",
    user = "root",
    pass = "example"
  })
}

resource "vault_generic_secret" "db_inventory_config" {
  path = "secret/db/mongo/inventory"
  data_json = jsonencode({
    host = "inventory-mongo-service",
    port = "27018",
    user = "root",
    pass = "example"
  })
}
