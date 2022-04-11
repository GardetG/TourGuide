From openjdk:11.0-jre-slim
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
copy ./build/libs/locationservice-1.0-SNAPSHOT.jar locationservice.jar
CMD ["java","-jar","locationservice.jar"]