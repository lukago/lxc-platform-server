FROM ubuntu:18.04

RUN apt-get update && apt-get -y install \
    openjdk-11-jre \
    openssh-server \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV PATH $JAVA_HOME/bin:$PATH

WORKDIR /usr/app

COPY /target/lxc-platform-server*.jar /usr/app/app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar app.jar" ]