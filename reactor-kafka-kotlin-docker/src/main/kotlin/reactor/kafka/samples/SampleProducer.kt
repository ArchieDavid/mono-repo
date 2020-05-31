package reactor.kafka.samples

import mu.KotlinLogging
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.IntegerSerializer
import org.apache.kafka.common.serialization.StringSerializer
import reactor.core.publisher.Flux
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


private val log = KotlinLogging.logger {}

class SampleProducer(bootstrapServers: String) {
    private val sender: KafkaSender<Int, String>
    private val dateFormat: SimpleDateFormat
    fun sendMessages(topic: String?, count: Int, latch: CountDownLatch) {
        sender.send(
                Flux.range(1, count)
                    .map { i: Int ->
                        SenderRecord.create(
                            ProducerRecord(topic, i, "Message_$i"), i
                        )
                    }
            )
            .doOnError { e: Throwable? ->
                log.error(
                    "Send failed",
                    e
                )
            }
            .subscribe { r: SenderResult<Int> ->
                val metadata = r.recordMetadata()
                println(
                    "Message ${r.correlationMetadata()} sent successfully, " +
                            "topic-partition=${metadata.topic()}-${metadata.partition()} offset=${metadata.offset()} " +
                            "timestamp=${dateFormat.format(Date(metadata.timestamp()))}"
                )
                latch.countDown()
            }
    }

    fun close() {
        sender.close()
    }

    companion object {
        private const val BOOTSTRAP_SERVERS = "localhost:9092"
        private const val TOPIC = "demo-topic"

        fun main(args: Array<String>) {
            val count = 10
            val latch = CountDownLatch(count)
            val producer = SampleProducer(BOOTSTRAP_SERVERS)
            producer.sendMessages(TOPIC, count, latch)
            latch.await(10, TimeUnit.SECONDS)
            producer.close()
        }
    }

    init {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ProducerConfig.CLIENT_ID_CONFIG] = "sample-producer"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
        val senderOptions = SenderOptions.create<Int, String>(props)
        sender = KafkaSender.create(senderOptions)
        dateFormat = SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy")
    }
}