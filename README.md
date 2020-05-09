# Kafka Client

Kafka Client is a vanilla java library that make it easy to consume data from kafka,
 a list of features:

* [x] Parallel consuming
* [x] Consuming retry
* [x] Consuming failover
* [x] Designed to be easy to mock and test
* [x] Designed to support slow consumers without re balancing
* [x] Designed to high throughput usage
* [x] Individual record consuming
* [x] Batch records consuming
* [x] Frameworkless, but easily configurable to someone
* [x] Commits are managed for you based on behavior

# Getting Started

```java
Consumers.<String, String>builder()
.prop(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
.prop(VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
.topics("stocks.changed")
.prop(GROUP_ID_CONFIG, "stocks")
.recoverCallback(ctx -> {
  // here you can send the message to another topic, send a SMS, etc.
  log.info("status=recovering, record={}", ctx.record().value());
})
.callback((ctx, record) -> {
  log.info("status=consumed, value={}", record.value());
})
.build()
.consume()
.waitFor();
```

# Examples
* [Vanilla][1]
* [Spring Framework][1]
* [Micronaut][1]
* [Quarkus][1]

[1]: 1
