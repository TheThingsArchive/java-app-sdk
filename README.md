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
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Arrays;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.thethingsnetwork.java.app.lib.Client;
import org.thethingsnetwork.java.app.lib.Message;

public class Test {

    public static void main(String[] args) throws MqttException, MalformedURLException, URISyntaxException {
        
        new Client("eu", "MyAppEUI", "MyAppSecret")
                .registerMessageHandler((Message t) -> System.out.println("new uplink data: " + Arrays.toString(t.getRawPayload())))
                .registerActivationHandler((Message t) -> System.out.println("new device activation: " + t.getString("dev_eui")))
                .registerErrorHandler((Throwable t) -> System.out.println("error: " + t))
                .registerConnectHandler((MqttClient t) -> System.out.println("connected !"))
                .start();
    }

}

```

To test this sample code, clone this repo (you can also download the zip):
```bash
git clone https://github.com/TheThingsNetwork/java-app-lib.git && cd java-app-lib/sample
```
Set your credentials in the java file:
```java
  String region = "eu";
  String appId = "";
  String accessKey = "";
```
Finally, run the Test class:
```bash
mvn clean compile exec:java -Dexec.mainClass="org.thethingsnetwork.java.app.sample.Test"
```
