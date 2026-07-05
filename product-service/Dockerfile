# --- Stage 1: Bygg applikationen och packa upp de lagrade jar-delarna ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Kopiera bara det som behövs för att lösa beroenden först — det här steget
# cachas och hoppas över vid ombyggnad om inte pom.xml faktiskt ändrats
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline

# Nu kopieras källkoden in och bygget körs
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Packa upp den lagrade jar-filen i sina beståndsdelar
# (Sedan Spring Boot 3.3 heter jarmode "tools" istället för "layertools")
# Jaren döps om till ett fast namn så att ENTRYPOINT nedan alltid hittar den
RUN cp target/*.jar application.jar && \
    java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# --- Stage 2: Minimal runtime-image ---
FROM eclipse-temurin:21-jre
WORKDIR /app

# Kör aldrig som root i en container — särskilt viktigt för OpenShift senare,
# som av säkerhetsskäl vägrar köra containers som root som standard
RUN addgroup --system spring && adduser --system --ingroup spring spring
USER spring:spring

COPY --from=build /app/extracted/dependencies/ ./
COPY --from=build /app/extracted/spring-boot-loader/ ./
COPY --from=build /app/extracted/snapshot-dependencies/ ./
COPY --from=build /app/extracted/application/ ./

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "application.jar"]