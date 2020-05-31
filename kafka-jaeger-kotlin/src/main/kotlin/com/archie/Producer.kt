package com.archie

import io.jaegertracing.Configuration
import io.jaegertracing.internal.JaegerTracer
import io.jaegertracing.internal.samplers.ConstSampler
import io.opentracing.contrib.kafka.TracingKafkaProducer
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import java.util.*

val testTopic = "source-topic"

fun main() {
    val tracerProducer = createTracer(Configuration("test-producer"))
    val producerProps = Properties().apply {
        this[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = "localhost:9092"
        this[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java.name
        this[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java.name
    }

    val producer = KafkaProducer<String, Int>(producerProps)
    val tracingKafkaProducer = TracingKafkaProducer<String, Int>(producer, tracerProducer)

    (0 until 10).forEach {
        tracingKafkaProducer.send(ProducerRecord(testTopic, "my-test-key-$it", it))
    }

    tracingKafkaProducer.close()
    tracerProducer.close()
    producer.close()
}

fun createTracer(producerConfig: Configuration): JaegerTracer {
    return producerConfig
        .withReporter(
            Configuration.ReporterConfiguration()
                .withLogSpans(true)
                .withFlushInterval(1000)
                .withMaxQueueSize(100)
        )
        .withSampler(
            Configuration.SamplerConfiguration()
                .withType(ConstSampler.TYPE)
                .withParam(1)
                .withManagerHostPort("jaeger:5778")
        )
        .tracerBuilder
        .build()
}
