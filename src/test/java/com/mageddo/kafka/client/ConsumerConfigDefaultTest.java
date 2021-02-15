package com.mageddo.kafka.client;

import org.junit.jupiter.api.Test;

import templates.ConsumerConfigTemplates;

import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_RECORDS_CONFIG;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class ConsumerConfigDefaultTest {

  @Test
  void validateDefaultConfigs() {

    // arrange
    final ConsumerConfigDefault<String, byte[]> consumerConfig = ConsumerConfigDefault
        .<String, byte[]>builder()
        .topics("topic")
        .build();

    // act
    final String props = consumerConfig
        .props()
        .toString();

    //assert
    assertEquals("{enable.auto.commit=false, bootstrap.servers=localhost:9092}", props);

  }

  @Test
  void mustCopy() {
    // arrange
    final ConsumerConfigDefault<String, byte[]> consumerConfig = ConsumerConfigDefault
        .<String, byte[]>builder()
        .topics("topic")
        .prop(MAX_POLL_RECORDS_CONFIG, 30)
        .build();

    // act
    final ConsumerConfigDefault<String, byte[]> copy = consumerConfig.toBuilder()
        .build();

    // assert
    assertEquals(String.valueOf(consumerConfig), String.valueOf(copy));
    assertEquals(3, consumerConfig.props()
        .size());
    assertEquals(3, copy.props()
        .size());
    assertNotEquals(System.identityHashCode(consumerConfig), System.identityHashCode(copy));
  }

  @Test
  void consumersConfigMustStayDisabled() {
    // arrange
    final ConsumerConfigDefault<String, byte[]> consumerConfig = ConsumerConfigDefault
        .<String, byte[]>builder()
        .consumers(Integer.MIN_VALUE)
        .consumers(5)
        .build();

    // act
    final int consumers = consumerConfig
        .toBuilder()
        .consumers(10)
        .build()
        .consumers();

    // assert
    assertEquals(Integer.MIN_VALUE, consumers);
  }

  @Test
  void consumerMustStayDisabled() {

    // arrange
    final ConsumerConfigDefault<String, byte[]> consumerConfig = ConsumerConfigTemplates
        .<String, byte[]>builder()
        .consumers(Integer.MIN_VALUE)
        .build();

    // act
    consumerConfig
        .toBuilder()
        .build()
        .consume();

    // assert

  }
}