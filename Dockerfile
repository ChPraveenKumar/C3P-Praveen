FROM camunda/camunda-bpm-platform
EXPOSE 8080
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
ADD target/SouthboundOnboarding-0.0.1-SNAPSHOT.jar /usr/src/app
CMD ["java","-jar","/usr/src/app/SouthboundOnboarding-0.0.1-SNAPSHOT.jar"]
