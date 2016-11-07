# API Reference

Require the TTN Client module:

```java
import org.thethingsnetwork.java.app.lib.Client;
```

## Class: Client

Creates an instance of the client:

```java
Client client = new Client(region, appId, accessKey [, connOpts]);
```

* `region [String]`: The region (e.g. `eu`) or full hostname (e.g. `eu.thethings.network`) of the handler to connect to.
* `appId [String]`: The ID of the application to connect to (e.g. `hello-world`).
* `appAccessKey [String]`: An access key for the application, formatted as base64 (e.g. `'2Z+MU0T5xZCaqsD0bPqOhzA6iygGFoi4FAgMFgBfXSo='`).
* `connOpts [MqttConnectOptions]`: Some custom configuration of the MQTT connection. This parameter is optional. For example to use TLS download [mqtt-ca.pem](https://preview.console.thethingsnetwork.org/mqtt-ca.pem) and trust it following this guide: [Trust self-signed certificates](http://howardism.org/Technical/Java/SelfSignedCerts.html):

## Event: connect

Emitted on successful connection.

```java
client.onConnected(new Consumer<MqttClient>() {
    public void accept(MqttClient client) {
        System.out.println("connected !");
    }
});
```

* `cb.client [MqttClient]`: MQTT connection wrapper. See [MQTT](http://www.eclipse.org/paho/files/javadoc/org/eclipse/paho/client/mqttv3/MqttClient.html).

## Event: error

Emitted when the client cannot connect or when a parsing error occurs.

```java
client.onError(new Consumer<Throwable>() {
    public void accept(Throwable error) {
        System.err.println("error: " + error.getMessage());
    }
});
```

* `cb.error [Throwable]`: Error object. See [MQTT](https://docs.oracle.com/javase/8/docs/api/java/lang/Exception.html).

## Event: message

Emitted when TTN forwards a message addressed to your application.

```java
client.onMessage(new BiConsumer<String, Object>() {
    public void accept(String devId, Object data) {
        System.out.println("Message: " + devId + " " + data);
});
```

* `cb.devId [String]`: Device ID, e.g.: `my-uno`.
* `cb.data [Object]`: Message data, e.g.:

  ```json
  {
    "port": 1,
    "counter": 10,
    "payload_raw": "MQ==",
    "payload_fields": {
      "led": true
    },
    "metadata": {
      "time": "2016-09-07T12:50:07.068771281Z",
      "frequency": 868.1,
      "modulation": "LORA",
      "data_rate": "SF7BW125",
      "coding_rate": "4/5",
      "gateways": [{
        "gtw_id": "0000024B08060112",
        "timestamp": 3955426155,
        "time": "2016-09-07T12:50:07.053048Z",
        "channel": 4,
        "rssi": -109,
        "snr": 5.8,
        "rf_chain": 1
      }]
    }
  }
  ```

### Listen for a specific device

```java
client.onMessage("my-uno", new BiConsumer<String, Object>() {
    public void accept(String devId, Object data) {
        System.out.println("Message: " + devId + " " + data);
});
```

### Listen for a specific field (and device)

```java
client.onMessage("my-uno", "led", new BiConsumer<String, Object>() {
    public void accept(String devId, Object data) {
        System.out.println("Message: " + devId + " " + data);
});
```

## Event: activation

Emitted when a device registered to the application activates.

```java
client.onActivation(new BiConsumer<String, JSONObject>() {
    public void accept(String _devId, JSONObject _data) {
        System.out.println("Activation: " + devId + " " + data);
    }
});
```

* `cb.devId [String]`: Device ID, e.g.: `my-uno`.
* `cb.data [Object]`: Activation data, e.g.:

  ```json
  {
    "app_eui": "70B3D57ED0000AFB",
    "dev_eui": "0004A30B001B7AD2",
    "dev_addr": "260023BB",
    "metadata": {
      "time": "2016-09-07T12:43:17.97454032Z",
      "frequency": 867.1,
      "modulation": "LORA",
      "data_rate": "SF7BW125",
      "coding_rate": "4/5",
      "gateways": [{
        "gtw_id": "0000024B08060112",
        "timestamp": 3546311603,
        "time": "2016-09-07T12:43:17.938537Z",
        "channel": 2,
        "rssi": -107,
        "snr": 1.2
      }]
    }
  }
  ```

## Event: device

Emitted when a device event is published.

```java
client.onDevice(null, "down/scheduled", new TriConsumer<String, String, JSONObject>() {
    public void accept(String devId, String event, JSONObject data) {
        System.out.println("Received event "+event+"for device "+devId);
    }
});
```

* `cb.devId [String]`: Device ID, e.g.: `my-uno`.
* `cb.event [String]`: Event name, e.g.: `down/scheduled`.
* `cb.data [Object]`: Event data, e.g. for `down/scheduled`:

  ```json
  {
    "port": 1,
    "payload_raw": "MQ==",
  }
  ```

## Method: send

Send a message to a specific device.

```java
client.send(devId, payload, port);
```

*  `deviceId [String]`: The ID of the device to address, e.g. `my-uno`
*  `payload [mixed]`: Message to send, either:
    *  Byte array, e.g. `[1]`
    *  ByteBuffer, e.g. `ByteBuffer.allocate(2).put(0x00)`
    *  JsonObject, e.g. `{ led: true }`
    
        > This requires your application to be configured with an encoder payload function to encode the object in bytes.
        
*  `port [Integer]`: Optional port to address. Default: `1`.

> See the [Java ByteBuffer reference](https://docs.oracle.com/javase/8/docs/api/java/nio/ByteBuffer.html) for different ways to create a buffer. The client will rewind the buffer before publishing the message to The Things Network's MQTT broker.

## Method: end

Close the client via [`client.close()`](http://www.eclipse.org/paho/files/javadoc/org/eclipse/paho/client/mqttv3/MqttClient.html#close--).

```java
client.end([timeout]);
```

* `timeout [Integer]`: The time you give to the client to close the connection. This parameter is optional. Default is 5000 ms.
