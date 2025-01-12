# Справочник по k8s

## Сбор контейнеров

1. Сбор контейнеров происходит из корневой директории проекта командой:

   ```bash
   docker buildx build --platform linux/amd64 -f ./<app-directory>/Dockerfile --tag fenrisv/<app-name>:<app-version> . --load 2>&1 | tee build.log
   ```
2. Для сохранения образов используется `Docker Hub`. Для публикации образа используется команда:
   ```bash
   docker push fenrisv/<app-name>:<app-version>
   ```

## Манифесты k8s

1. Установить Ingress:

   ```bash
   helm upgrade --install ingress-nginx ingress-nginx --repo https://kubernetes.github.io/ingress-nginx --namespace ingress-nginx --create-namespace
   ```
2. Прежде всего необходимо развернуть манифесты для создания неймспейсов и ConfigMap:

   ```bash   
   kubectl apply -f k8s/namespaces
   kubectl apply -f k8s/config-map.yaml
   ```
3. Далее порядок не столь важен и необходимо развернуть все остальные манифесты:

   ```bash
   kubectl apply -f k8s/ingress-service.yaml
   kubectl apply -f k8s/config-map.yaml
   kubectl apply -f k8s/volumes
   kubectl apply -f k8s/daemons
   kubectl apply -f k8s/statefuls
   kubectl apply -f k8s/deployments
   ```

## Создание кластера PostgreSQL (не используется)

1. Добавить Helm репозиторий:

   ```bash
   helm repo add bitnami https://charts.bitnami.com/bitnami
   helm repo update
   ```
2. Установить кластерную PostgreSQL:

   ```bash
   helm install my-postgresql bitnami/postgresql --namespace pg --create-namespace -f pg/values.yaml
   ```
3. Получить пароль:

   ```bash
   kubectl get secret --namespace pg my-postgresql -o jsonpath="{.data.postgres-password}" | base64 --decode
   ```
