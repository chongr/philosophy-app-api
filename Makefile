build:
	mvn package

server:
	heroku local

run:
	mvn package && heroku local