package com.mageddo.kafka.client;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static org.apache.kafka.clients.consumer.ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

@Value
@Builder
@Accessors(chain = true, fluent = true)
@AllArgsConstructor
public class ConsumerConfig<K, V> implements ConsumerCreateConfig<K, V>, ConsumingConfig<K, V> {

  /**
   * @see ConsumerCreateConfig#props()
   */
  private Map<String, Object> props;

  /**
   * @see ConsumerCreateConfig#consumers()
   */
  private int consumers;

  /**
   * @see ConsumerCreateConfig#topics()
   */
  @NonNull
  private Collection<String> topics;

  /**
   * @see ConsumingConfig#pollTimeout()
   */
  @NonNull
  private Duration pollTimeout;

  /**
   * @see ConsumingConfig#pollInterval()
   */
  @NonNull
  private Duration pollInterval;

  /**
   * @see ConsumingConfig#retryPolicy()
   */
  @NonNull
  private RetryPolicy retryPolicy;

  /**
   * @see ConsumingConfig#recoverCallback() )
   */
  private RecoverCallback<K, V> recoverCallback;

  /**
   * @see ConsumingConfig#callback()
   */
  private ConsumeCallback<K, V> callback;

  /**
   * @see ConsumingConfig#batchCallback()
   */
  private BatchConsumeCallback<K, V> batchCallback;

  /**
   * @see ConsumerCreateConfig#consumerSupplier()
   */
  private ConsumerSupplier<K, V> consumerSupplier;

  public ConsumerFactory<K, V> consume(){
    return Consumers.consume(this);
  }

  public ConsumerConfig<K, V> prop(String k, Object v) {
    this.props.put(k, v);
    return this;
  }

  public Map<String, Object> props() {
    this.prop(ENABLE_AUTO_COMMIT_CONFIG, false);
    this.prop(BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    return Collections.unmodifiableMap(this.props);
  }

  public ConsumerConfigBuilder<K, V> toBuilder() {
    return new ConsumerConfigBuilder<K, V>()
        .consumers(this.consumers)
        .topics(this.topics)
        .pollTimeout(this.pollTimeout)
        .pollInterval(this.pollInterval)
        .retryPolicy(this.retryPolicy)
        .recoverCallback(this.recoverCallback)
        .callback(this.callback)
        .batchCallback(this.batchCallback)
        .props(this.props)
        ;
  }

  @Slf4j
  public static class ConsumerConfigBuilder<K, V> {

    public ConsumerConfigBuilder() {
      this.props = new LinkedHashMap<>();
      this.consumers = 1;
      this.pollTimeout = DefaultConsumingConfig.DEFAULT_POLL_TIMEOUT;
      this.pollInterval = DefaultConsumingConfig.FPS_30_DURATION;
      this.retryPolicy = DefaultConsumingConfig.DEFAULT_RETRY_STRATEGY;
      this.topics = Collections.EMPTY_LIST;
      this.consumerSupplier = ConsumerFactory.defaultConsumerSupplier();
    }

    public ConsumerConfigBuilder<K, V> consumers(int consumers) {
      if(this.consumers == Integer.MIN_VALUE){
        log.info(
            "consumer was previously disabled by set it's value to {}, it won't be re enabled. groupId={}, topics={}",
            Integer.MIN_VALUE,
            this.props.get(GROUP_ID_CONFIG),
            this.topics
        );
        return this;
      }
      this.consumers = consumers;
      return this;
    }
    public ConsumerConfigBuilder<K, V> topics(String... topics) {
      this.topics = Arrays.asList(topics);
      return this;
    }

    public ConsumerConfigBuilder<K, V> topics(Collection<String> topics) {
      this.topics = topics;
      return this;
    }

    public ConsumerConfigBuilder<K, V> prop(String key, Object value) {
      this.props.put(key, value);
      return this;
    }

    public ConsumerConfig<K, V> build() {
      return new ConsumerConfig<>(
          this.props,
          this.consumers,
          this.topics,
          this.pollTimeout,
          this.pollInterval,
          this.retryPolicy,
          this.recoverCallback,
          this.callback,
          this.batchCallback,
          this.consumerSupplier
      );
    }

  }
}
