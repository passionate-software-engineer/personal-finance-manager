## Personal Finance Manager

System allowing users to managed their personal finances.

## Tech/framework used

<b>Frontend</b>
- [AngularJS](https://angularjs.org/)
- [Bootstrap](https://getbootstrap.com/)
- [REST Assured](http://rest-assured.io/)
- [npm](https://www.npmjs.com/)

<b>Backend</b>
- [H2](http://www.h2database.com)
- [Spring data](https://projects.spring.io/spring-data/)
- [JPA](http://www.oracle.com/technetwork/java/index.html)
- [Maven](https://maven.apache.org/)
- [JUnit](https://maven.apache.org/)
- [JUnit Params](https://github.com/junit-team/junit4/wiki/parameterized-tests)
- [Mockito](http://site.mockito.org/)
- [Swagger](https://swagger.io/)
- [Hibernate](http://hibernate.org/)
- [Lombok](https://projectlombok.org/)

<b>Tests</b>
- [Gradle](https://gradle.org/)
- [Cucumber](https://cucumber.io/)
- [REST Assured](http://rest-assured.io/)
- [Selenium](https://www.seleniumhq.org/)
- [TestNG](https://testng.org)

<b>Other</b>
- [Jenkins](https://jenkins.io/)
- [Sonar](https://www.sonarqube.org/)
- [Docker](https://www.docker.com/)
- [Ansible](https://www.ansible.com/)
- [AWS](https://aws.amazon.com/)

## Code style
[![js-standard-style](https://img.shields.io/badge/code%20style-Google_Style-brightgreen.svg?style=flat)](https://github.com/checkstyle/checkstyle)


## Installation and Start-up

1. Clone project<br/>
2. Install Java JDK at least 1.8 version(http://www.oracle.com/technetwork/java/index.html)
2. Run Terminal<br/>
3. Go to the ../personal-finance-manager/backend directory
4. run "./gradlew build"
5. run "./gradlew bootRun"
6. Go to the ../personal-finance-manager/frontend directory
7. Install [NodeJS](https://nodejs.org/en/) and [Angular CLI](https://cli.angular.io/). 
8. run "npm install"
9. run "ng serve --open"

To stop backend proces go to de backend directory and run: "./gradlew --stop"

In application you can choose between 2 databases:
- H2(set by default)
- PostgreSQL
To change the database you can change configuration in application.yaml file.

## API Reference
Start the application and open the URL for API Documentation http://localhost:8088/swagger-ui.html
![Swagger API](http://ec2-13-59-117-184.us-east-2.compute.amazonaws.com:8088/swagger-ui.html)

## TODO - Tests
We have three different types of tests : JUnit, integrations, and E2E tests.<br/>
To run e2e tests :<br/>
**1)** Run main application<br/>
**2)** Build E2E project from gradle build file (in directory: ../frontend-test/build.gradle ) as separate project. <br/>
**3)** Enable annotation processing for lombok. <br/>
![Enable annotations](https://github.com/pio-kol/accouting-system/blob/master/readme/annotatnion.png)
**4)** Run e2e tests as TestNG.<br/>


