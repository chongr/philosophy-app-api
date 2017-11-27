# Overview

Philosophy App Api, hosted on [heroku](http://philosophy-app-api.herokuapp.com/)

# Running The Application

To test the example application run the following commands.

* To package the example run the following from the root dropwizard directory.

        mvn package

* Set up a .env file with local environement variables, and then run the following to test locally

        heroku local

# Future Work

Since the paths are deterministic, the application can be significantly sped up by checking the database for paths that have already been visited.  If a url is found while crawling to philosophy that is already in the db that information can be used to "shortcut" to the path for philosophy.

Also adding tests to the project.
