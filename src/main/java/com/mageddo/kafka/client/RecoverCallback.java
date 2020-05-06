package com.mageddo.kafka.client;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface RecoverCallback<K, V> {
  void recover(ConsumerRecord<K, V> record);
}
