FROM eclipse-temurin:8-jre-alpine

WORKDIR /gestaoHospital
COPY target/*.jar /gestaoHospital/hospital.jar

EXPOSE 8080

CMD ["java", "-jar", "/gestaoHospital/hospital.jar"]