# TourGuide
![alt text](https://user.oc-static.com/upload/2019/10/22/15717497085899_15717361053964_image1.jpg)  

TourGuide is a mobile an desktop application designed for the travelers and tourists. With TourGuide, discover the  touristic attractions around you, collect rewards points and use them to earn trip deals.
With TourGuide, change your way of travelling.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.
The JavaDoc is available [here](https://gardetg.github.io/TourGuide/).

### Prerequisities

This project is built with:

1. Framework: Spring Boot v2.6.5
2. Gradle: 7.4.1
3. Java 11
4. Docker: 20.10.12
5. Docker-compose: 1.29.2

### Structure

TourGuide is a microservice application build with Gradle and is intended to be deployed with Docker.
The main gradle project is composed of 5 module, one for each service:
- TourGuideService
- LocationService
- RewardService
- TripService
- UserService

The `shared` module contains common references used by the different services. 
 
![alt text](https://github.com/GardetG/TourGuide/blob/release/1.0/docs/diagram/diagram.jpg?raw=true)


### Setup the Application

#### Test and Build each services:

In the root directory of the project, use Gradle build task to perform unit tests and build each services: `.\gradlew build`  
Each project can be build independently with: `.\gradlew <ServiceName>:build`  
Test and coverage reports and the packaged jar can be found in the `\build` directory of each service.  

#### Build Docker Images

Dockerfile are present in each service directory and should be build with: `docker image build -t <serviceName> .`  
Please, make sure to name each container image in accordance with the `docker-compose.yml` present in the project root directory.   

### Run the Application

#### Integration and Performance tests:

TourGuideService integration and performance tests use a custom`docker-compose.yml` present in `\src\main\resources\integrationTest\`.  
To perform the integration tests, use Gradle integrationTest task: `.\gradlew integration`  
To perform the performance tests, use Gradle integrationTest task: `.\gradlew performance`  

#### Running the Application

On the project root directory where the `docker-compose.yml` file is present, execute: `docker-compose up`