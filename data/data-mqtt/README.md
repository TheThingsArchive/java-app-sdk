# The Things Network Java Client [![Build Status](https://travis-ci.org/TheThingsNetwork/java-app-lib.svg?branch=master)](https://travis-ci.org/TheThingsNetwork/java-app-lib) [![MAVEN](https://img.shields.io/maven-central/v/org.thethingsnetwork/java-app-lib.svg)](http://mvnrepository.com/artifact/org.thethingsnetwork/java-app-lib)

This is the Java client for [The Things Network](https://www.thethingsnetwork.org) to receive activations and messages from IoT devices via The Things Network and send messages as well.

## Maven [![MAVEN](https://img.shields.io/maven-central/v/org.thethingsnetwork/java-app-lib.svg)](http://mvnrepository.com/artifact/org.thethingsnetwork/java-app-lib)

Depend on the artifact via [Maven](http://mvnrepository.com/artifact/org.thethingsnetwork/java-app-lib):

```xml
<dependency>
  <groupId>org.thethingsnetwork</groupId>
  <artifactId>java-app-lib</artifactId>
  <version>1.0.0</version>
</dependency>
```

## Documentation

A Quick Start and full API Reference can be found in [The Things Network Documentation](https://www.thethingsnetwork.org/docs/refactor/java/).

## Smaple & Test [![Build Status](https://travis-ci.org/TheThingsNetwork/java-app-lib.svg?branch=master)](https://travis-ci.org/TheThingsNetwork/java-app-lib)

A [sample app](sample/src/main/java/org/thethingsnetwork/java/app/sample/Test.java) is included. To run:

1.  Install [OpenJDK](http://openjdk.java.net/install/) or [Java](https://www.java.com/en/download/).
2.  [Download](http://maven.apache.org/download.cgi) and [Instal](http://maven.apache.org/install.html) Maven.
3.  [Download](https://github.com/TheThingsNetwork/java-app-lib/archive/master.zip) or [clone](https://help.github.com/articles/which-remote-url-should-i-use/) the repository.
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
6.  Build and run the Test class:

    ```bash
    cd sample
    mvn clean compile exec:java -Dexec.mainClass="org.thethingsnetwork.java.app.sample.App"
    ```
