# The Things Network Java MQTT Client [![Build Status](https://travis-ci.org/TheThingsNetwork/java-app-sdk.svg?branch=master)](https://travis-ci.org/TheThingsNetwork/java-app-sdk) [![MAVEN](https://img.shields.io/maven-central/v/org.thethingsnetwork/data-mqtt.svg)](http://mvnrepository.com/artifact/org.thethingsnetwork/data-mqtt)

This is the Java MQTT client for [The Things Network](https://www.thethingsnetwork.org) to receive activations and messages from IoT devices via The Things Network and send messages as well.

## Maven [![MAVEN](https://img.shields.io/maven-central/v/org.thethingsnetwork/data-mqtt.svg)](http://mvnrepository.com/artifact/org.thethingsnetwork/data-mqtt)

Depend on the artifact via [Maven](http://mvnrepository.com/artifact/org.thethingsnetwork/data-mqtt):

```xml
<dependency>
  <groupId>org.thethingsnetwork</groupId>
  <artifactId>data-mqtt</artifactId>
  <version>2.1.3</version>
</dependency>
```

## Documentation

A Quick Start and full API Reference can be found in [The Things Network Documentation](https://www.thethingsnetwork.org/docs/refactor/java/).

## Smaple & Test [![Build Status](https://travis-ci.org/TheThingsNetwork/java-app-sdk.svg?branch=master)](https://travis-ci.org/TheThingsNetwork/java-app-sdk)

A [sample app](samples/mqtt/src/main/java/org/thethingsnetwork/samples/mqtt/App.java) is included. To run:

1.  Install [OpenJDK](http://openjdk.java.net/install/) or [Java](https://www.java.com/en/download/).
2.  [Download](http://maven.apache.org/download.cgi) and [Instal](http://maven.apache.org/install.html) Maven.
3.  [Download](https://github.com/TheThingsNetwork/java-app-sdk/archive/master.zip) or [clone](https://help.github.com/articles/which-remote-url-should-i-use/) the repository.
4.  Build and cache the artifact:

    ```bash
    mvn clean package install
    ```

5.  export your region, Application ID and Access Key as environment variables.

    ```bash
    export region="eu"
    export appId="my-app-id"
    export accessKey="my-access-key"
    ```
6.  Build and run the MQTT sample:

    ```bash
    cd samples/mqtt
    mvn clean compile exec:java -Dexec.mainClass="org.thethingsnetwork.samples.mqtt.App"
    ```
