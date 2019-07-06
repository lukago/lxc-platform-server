FROM alpine:3.10

RUN apk --no-cache add \
    openjdk11-jre

WORKDIR /usr/app

COPY /target/lxc-platform-server*.jar /usr/app/app.jar

ENTRYPOINT [ \
  "sh", \
  "-c", \
  "java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:$DEBUG_PORT -jar app.jar" \
]