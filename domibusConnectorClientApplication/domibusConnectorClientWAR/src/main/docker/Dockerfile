FROM java:openjdk-8-ubi8
ENV JAVA_TEMPDIR=/app/temp
ENV JAVA_OPTS="-XX:MetaspaceSize=256M"
COPY app /app/
USER root

RUN mkdir -p /app/config /app/data /app/logs /app/temp /app/lib
#RUN echo "export JAVA_OPTS=\"\$JAVA_OPTS\"" >> /app/run.sh \
RUN echo "exec java \$JAVA_OPTS -Dloader.path=/app/lib -Dspring.config.name=connector-client -cp /app/connector-client.war org.springframework.boot.loader.PropertiesLauncher" >> /app/run.sh

RUN groupadd --system connector-client && useradd --system -g connector-client connector-client && chown -R connector-client:connector-client /app
WORKDIR /app
USER connector-client
EXPOSE 8080
VOLUME /app/config
VOLUME /app/data
VOLUME /app/logs
VOLUME /app/temp
VOLUME /app/lib

CMD ["/bin/sh","/app/run.sh", "run"]