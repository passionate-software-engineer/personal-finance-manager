# Personal Finance Manager

System allowing users to manage their personal finances.
Define accounts you are using, define categories and you are ready to save your transaction.

## Technologies & Frameworks

### Frontend
- [Angular](https://angular.io/)
- [Bootstrap](https://getbootstrap.com/)
- [npm](https://www.npmjs.com/)

### Backend
- [H2](http://www.h2database.com)
- [Spring data](https://projects.spring.io/spring-data/)
- [Gradle](https://gradle.org/)
- [JUnit](https://maven.apache.org/)
- [JUnit Params](https://github.com/junit-team/junit4/wiki/parameterized-tests)
- [Mockito](http://site.mockito.org/)
- [Swagger](https://swagger.io/)
- [Hibernate](http://hibernate.org/)
- [Lombok](https://projectlombok.org/)
- [Spring Boot](https://spring.io/projects/spring-boot)

### Tests
- [Gradle](https://gradle.org/)
- [REST Assured](http://rest-assured.io/)
- [Selenium](https://www.seleniumhq.org/)
- [TestNG](https://testng.org)

### Automation
- [Jenkins](https://jenkins.io/)
- [AWS](https://aws.amazon.com/)
- [Bash](https://www.gnu.org/software/bash/)

### Code Quality
- [Checkstyle](http://checkstyle.sourceforge.net/)
- [FindBugs](http://findbugs.sourceforge.net/)
- [PMD](https://pmd.github.io/)
- [Jacoco](https://www.eclemma.org/jacoco/)

## How to start project locally

1. Install [JDK 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), [NodeJS](https://nodejs.org/en/), [Angular CLI](https://cli.angular.io/)
2. Open terminal window, go to <b><i>backend</i></b> directory & run <b><i>./gradlew bootRun</i></b>
3. Open second terminal window, go to <b><i>frontend</i></b> directory, run <b><i>npm install</i></b> & <b><i>ng serve --open</i></b>
4. Browser window will open automatically, you can play with the application

## API Reference
[Swagger API](http://ec2-13-59-117-184.us-east-2.compute.amazonaws.com:8088/swagger-ui.html)

## Demo instance
[Personal Finance Manager Demo](http://personal-finance-manager.s3-website.us-east-2.amazonaws.com/accounts)

## Running application in IntelliJ
- Open IntelliJ, click open project, select <b><i>build.gradle</i></b> file, click <b><i>import as project</i></b>
- Enable annotation processing for Lombok. 
![Enable annotations for Lombok](https://github.com/pio-kol/accouting-system/blob/master/readme/annotatnion.png)
- Wait for Gradle to download half of the internet :)



