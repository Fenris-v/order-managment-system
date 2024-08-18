# Запуск приложения локально в minikube (windows).

Для запуска приложения в minikube, предварительно должны быть
установлены [Docker Desktop](https://www.docker.com/products/docker-desktop/)
и [Minikube](https://kubernetes.io/ru/docs/tasks/tools/install-minikube/)

1. Запустить Docker Desktop.
2. Запустить Minikube

    ```shell
    minikube start --vm-driver=docker
    ```

3. При желании запустить графическую оболочку Minikube Dashboard
    ```shell
    minikube dashboard
    ```
4. 
