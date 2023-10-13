FROM openjdk:17-oracle
VOLUME /tmp
COPY /build/libs/*SNAPSHOT.jar /app/product-service.jar
ENTRYPOINT ["java","-jar","/app/product-service.jar"]