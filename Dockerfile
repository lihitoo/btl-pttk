FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /project
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package

FROM tomcat:9.0-jdk17-temurin
ENV JAVA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Bangkok"
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /project/target/restman6.war /usr/local/tomcat/webapps/restman6.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
