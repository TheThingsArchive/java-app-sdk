# The Things Network Java SDK [![Build Status](https://travis-ci.org/TheThingsNetwork/java-app-sdk.svg?branch=master)](https://travis-ci.org/TheThingsNetwork/java-app-sdk)

This is the Java SDK for [The Things Network](https://www.thethingsnetwork.org) to interact with The Things Network backend.

**Please be aware that this sdk is under heavy development !**

## Modules

- [Account](account) - Interact with The Things Network account server
- [Management](management) - Interact with The Things Network Handler via the API
- [Data AMQP](data/amqp) - Subscribe to Things Network Handler to send/receive data via AMQP
- [Data MQTT](data/mqtt) - Subscribe to Things Network Handler to send/receive data via MQTT
- [Samples](samples) - Samples of how to use previous libraries

## Documentation

- [The Things Network Documentation](https://www.thethingsnetwork.org/docs/applications/java/)
- [Javadoc](https://thethingsnetwork.github.io/java-app-sdk/)

## Building in Docker

To build the Things Network Java SDK from the sources Docker can be used as follows

```
docker build -t java_builder .
docker run -it -v ///c/<your windows directory to the TTN java sdk>:/java-app-sdk java_builder
<docker-container-prompt>:/# cd java-app-sdk/
<docker-container-prompt>:/java-app-sdk# ./run.sh
```
  
