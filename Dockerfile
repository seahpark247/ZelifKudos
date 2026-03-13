FROM eclipse-temurin:17-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew assemble -Dgrails.env=production --no-daemon

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.war app.war
EXPOSE 8080
ENV JAVA_OPTS="-Xmx256m"
CMD ["sh", "-c", "java $JAVA_OPTS -Dgrails.env=production -jar app.war"]
