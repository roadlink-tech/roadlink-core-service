setup:
	sudo apt-get remove docker-compose
	sudo rm /usr/local/bin/docker-compose
	DESTINATION=/usr/local/bin/docker-compose
	sudo curl -L https://github.com/docker/compose/releases/download/v2.22.0/docker-compose-$(uname -s)-$(uname -m) -o $DESTINATION
	sudo chmod 755 $DESTINATION
	## setup de gradle 7.4, de docker, etc

setup_dev_env:
	docker-compose up dynamo localstack

build:
	./gradlew clean build -x test --no-daemon

test:
	./gradlew clean test

deploy:
	-docker rmi roadlink-core-service
	docker-compose build
	docker-compose up