
# Football Administrator Tool (RESTful) (Assessment #2)

## Description
⚽️ The Football Administrator Tool is a RESTful application oriented to admin a Football Teams & Players with a NoSQL Data Base to store data. ⚽️

## Requirements
- Java 11
- Gradle Build Tool (7 or greater)
- IDE (IntelliJ or Spring Tool Suite)
- Docker CLI or Docker Desktop
- Postman (Latest version to support Collection v2.1)

## Configuration
1 - Follow and execute the instructions to prepare the NoSQL Data Base with MongoDB from the following repository: https://github.com/georgeous497git/dockerMongoFootballAdmin


2 - Follow and execute the instructions to prepare the Postman scripts for testing purpose from the following repository:
https://github.com/georgeous497git/postmanFootballAdmin


## Execution
1 - Create a folder where you want to download the project. Enter to the folder created.


2 - Clone the project into your local machine.
```sh
git clone git@github.com:georgeous497git/footballAdmin.git
```


3 - Once you downloaded the project enter to the folder **footballAdmin** and execute the following command. (The project is already configured with basic configuration.)
```sh
cd footballAdmin
```
```sh
./gradlew bootRun
```


4 - In the console a banner will be printed (Football Team).
<img src="https://github.com/georgeous497git/footballAdmin/blob/develop/gradlewBootrun.png" width="800" height="250">

5 - **Execute the desired operations using swagger or the Postman Scripts.** 🤖

## Validation
The project is already configured to use the **localhost** and the port **8082** to be able to use the Swagger Tool.

Example of URI for Swagger:
```sh
http://localhost:8082/football-admin/swagger-ui/index.html
```
<img src="https://github.com/georgeous497git/footballAdmin/blob/develop/swaggerFootballAdmin.png" width="800" height="350">

#### TODO:
1 - Improve the Test Coverage.

2 - Improve the logging messages

3 - Write the log file & output data file into a dedicated folder for each one.

4 - Add Catching Exceptions (Due to deadline)
___________________________________________________________________________________
Thanks.
