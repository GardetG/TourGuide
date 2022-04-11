From openjdk:11.0-jre-slim
ENV TZ=Europe/Paris
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
copy ./build/libs/rewardservice-1.0-SNAPSHOT.jar rewardservice.jar
CMD ["java","-jar","rewardservice.jar"]