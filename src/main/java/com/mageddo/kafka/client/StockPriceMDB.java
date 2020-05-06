package com.mageddo.kafka.client;

import io.quarkus.runtime.StartupEvent;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.time.Duration;
import java.util.Collections;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class StockPriceMDB {

  public static final String EVERY_5_SECONDS = "0/5 * * * * ?";

  private final ConsumerFactory consumerFactory;
  private final Producer<String, byte[]> producer;

  public void consume(@Observes StartupEvent ev) {
    log.info("status=consume fired");
    final ConsumerConfig<String, byte[]> consumerConfig = new ConsumerConfig<String, byte[]>()
        .setTopics(Collections.singletonList("stock_changed"))
        .setGroupId("stock_client")
        .withProp(MAX_POLL_INTERVAL_MS_CONFIG, (int) Duration.ofMinutes(1).toMillis())
        .withProp(GROUP_ID_CONFIG, "stock_client")
        .withProp(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
        .withProp(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName())
        .withProp(VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName())
        .setRetryPolicy(RetryPolicy
            .builder()
            .maxTries(3)
            .delay(Duration.ofSeconds(29))
            .build()
        )
        .setBatchCallback((consumer, records, e) -> {
          for (final var record : records) {
            throw new RuntimeException("an error occurred");
//            log.info("key={}, value={}", record.key(), new String(record.value()));
          }
        });

    this.consumerFactory.consume(consumerConfig);
  }

  @Scheduled(cron = EVERY_5_SECONDS)
  void notifyStockUpdates(ScheduledExecution execution) {
    producer.send(new ProducerRecord<>(
        "stock_changed",
        String.format("stock=PAGS, price=%.2f", Math.random() * 100)
            .getBytes()
    ));
    log.info(
        "status=scheduled, scheduled-fire-time={}, fire-time={}",
        execution.getScheduledFireTime(),
        execution.getFireTime()
    );
  }
}
