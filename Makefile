build:
	mvn package

server:
	heroku local

run:
	mvn package -Dmaven.test.skip=true && heroku local