package com.archie

import io.jaegertracing.Configuration
import io.opentracing.contrib.kafka.TracingKafkaConsumer
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.consumer.KafkaConsumer
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import java.time.Duration
import java.util.*

fun main() {
    val tracerConsumer = createTracer(Configuration("test-consumer"))
    val consumerProps = Properties().apply {
        this[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        this[ConsumerConfig.GROUP_ID_CONFIG] = "KafkaExampleConsumer"
        this[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java.name
        this[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = IntegerDeserializer::class.java.name
    }

    val consumer = KafkaConsumer<String, Int>(consumerProps)
    val tracingKafkaConsumer = TracingKafkaConsumer(consumer, tracerConsumer)
    tracingKafkaConsumer.subscribe(listOf(testTopic))

    while (true) {
        val consumerRecords = tracingKafkaConsumer.poll(Duration.ofMillis(1000))
        consumerRecords.forEach { record ->
            println("key: " + record.key())
            println("value: " + record.value().toString())
            record.headers().forEach {
                println("headerKey: ${it.key()}")
                println("headerValue: ${String(it.value())}")
            }
        }
    }
}