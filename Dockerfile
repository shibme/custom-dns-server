FROM alpine
LABEL maintainer="shibme"
RUN mkdir workspace
WORKDIR workspace
RUN apk add --no-cache openjdk8-jre
ADD /target/custom-dns-server.jar /workspace/custom-dns-server.jar
EXPOSE 53
CMD ["java", "-jar", "custom-dns-server.jar"]