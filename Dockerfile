# 构建阶段
FROM maven:3.6-openjdk-8 AS builder
COPY ./pom.xml /usr/src/app/pom.xml
COPY ./src /usr/src/app/src
COPY ./config/settings.xml /etc/maven/settings.xml
WORKDIR /usr/src/app
RUN mvn dependency:go-offline --settings /etc/maven/settings.xml
RUN mvn -f /usr/src/app/pom.xml clean package --settings /etc/maven/settings.xml -DskipTests

# 运行时设置
FROM openjdk:11
WORKDIR /opt
RUN mkdir logs
RUN chmod -R 777 logs
ENV TZ=Asia/Shanghai
COPY --from=builder /usr/src/app/target/*.jar /opt/
EXPOSE 9900
CMD java -jar /opt/chromatic_origin.jar --spring.profiles.active=server