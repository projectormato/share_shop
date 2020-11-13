setup:
	docker-compose up -d db
test:
	./gradlew test
run:
	./gradlew bootRun
