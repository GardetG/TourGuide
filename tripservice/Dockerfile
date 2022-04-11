From openjdk:11.0-jre-slim
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
copy ./build/libs/tripservice-1.0-SNAPSHOT.jar tripservice.jar
CMD ["java","-jar","tripservice.jar"]