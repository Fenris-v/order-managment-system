install-ingress:
	helm upgrade --install ingress-nginx ingress-nginx --repo https://kubernetes.github.io/ingress-nginx --namespace ingress-nginx --create-namespace
apply-mans:
	kubectl apply -f ./ingress-service.yaml
	kubectl apply -f ./config-map.yaml
	kubectl apply -f ./volumes
	kubectl apply -f ./daemons
	kubectl apply -f ./statefuls
	kubectl apply -f ./deployments
