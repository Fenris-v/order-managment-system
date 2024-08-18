# Сборка и публикация образов.

1. Для сборки модуля используем команду из корня проекта
    ```shell
    docker buildx build --platform linux/amd64 --tag <username>/<imagename>:latest -f <module>/Dockerfile . --load 2>&1
    ```
2. Для публикации используем команду:
   ```shell
   docker push <username>/<imagename>:latest
   ```
