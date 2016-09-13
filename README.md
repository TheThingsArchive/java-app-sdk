# The Things Network Java Client [![MAVEN](https://img.shields.io/maven-central/v/org.thethingsnetwork/java-app-lib.svg)](http://mvnrepository.com/artifact/org.thethingsnetwork/java-app-lib)

This is the Java client for [The Things Network](https://www.thethingsnetwork.org) to receive activations and messages from IoT devices via The Things Network and send messages as well.

## Maven

```xml
<dependency>
  <groupId>org.thethingsnetwork</groupId>
  <artifactId>java-app-lib</artifactId>
  <version>0.1</version>
</dependency>
```

## Documentation

A Quick Start and full API Reference can be found in [The Things Network Documentation](https://www.thethingsnetwork.org/docs/refactor/java/).

## Example

```java
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.thethingsnetwork.java.app.lib.Client;
import org.thethingsnetwork.java.app.lib.Mesage;
import org.thethingsnetwork.java.app.lib.Region;
import org.thethingsnetwork.java.app.lib.handlers.ActivationHandler;
import org.thethingsnetwork.java.app.lib.handlers.ConnectHandler;
import org.thethingsnetwork.java.app.lib.handlers.ErrorHandler;
import org.thethingsnetwork.java.app.lib.handlers.MessageHandler;

public class Test {

    public static void main(String[] args) throws MqttException {
        new Client(Region.STAGING, "MyAppEUI", "MyAppSecret")
                .registerMessageHandler((Mesage t) -> {
                    System.out.println("new message: " + t.payload);
                })
                .registerActivationHandler((Mesage t) -> {
                    System.out.println("new activation: " + t.payload);
                })
                .registerErrorHandler((Throwable t) -> {
                    System.out.println("error: " + t);
                })
                .registerConnectHandler((MqttClient t) -> {
                    System.out.println("connected !");
                })
                .start();
    }

}

```
