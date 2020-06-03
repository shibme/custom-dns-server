FROM openjdk:11-jre-slim
LABEL maintainer="shibme"
WORKDIR workspace
ADD /target/custom-dns-server.jar /workspace/custom-dns-server.jar
EXPOSE 53
CMD ["java", "-jar", "custom-dns-server.jar"]