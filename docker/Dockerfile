FROM azul/zulu-openjdk-alpine:11.0.7-jre-headless

# Fix error apache poi, FontManagerFactory
RUN apk add --update ttf-dejavu && rm -rf /var/cache/apk/*

ARG JAR_FILE=app.jar
COPY ${JAR_FILE} app.jar
ENV JVM_OPT " "
ENTRYPOINT ["sh", "-c", "java -server -Djava.security.egd=file:/dev/./urandom $JVM_OPT -jar /app.jar"]