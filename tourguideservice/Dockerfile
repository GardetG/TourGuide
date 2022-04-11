From openjdk:11.0-jre-slim
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
copy ./build/libs/tourguideservice-0.0.1-SNAPSHOT.jar tourguideservice.jar
CMD ["java","-jar","tourguideservice.jar"]