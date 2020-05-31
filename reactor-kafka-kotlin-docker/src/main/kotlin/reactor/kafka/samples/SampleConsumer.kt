package reactor.kafka.samples

import mu.KotlinLogging
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.IntegerDeserializer
import org.apache.kafka.common.serialization.StringDeserializer
import reactor.core.Disposable
import reactor.kafka.receiver.KafkaReceiver
import reactor.kafka.receiver.ReceiverOptions
import reactor.kafka.receiver.ReceiverPartition
import reactor.kafka.receiver.ReceiverRecord
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

private val log = KotlinLogging.logger {}

class SampleConsumer(bootstrapServers: String) {
    private val receiverOptions: ReceiverOptions<Int, String>
    private val dateFormat: SimpleDateFormat
    fun consumeMessages(topic: String, latch: CountDownLatch): Disposable {
        val options = receiverOptions.subscription(setOf(topic))
            .addAssignListener { partitions: Collection<ReceiverPartition?>? ->
                log.debug(
                    "onPartitionsAssigned {}",
                    partitions
                )
            }
            .addRevokeListener { partitions: Collection<ReceiverPartition?>? ->
                log.debug(
                    "onPartitionsRevoked {}",
                    partitions
                )
            }
        val kafkaFlux = KafkaReceiver.create(options).receive()
        return kafkaFlux.subscribe { record: ReceiverRecord<Int, String> ->
            val offset = record.receiverOffset()
            println(
                "Received message: topic-partition=${offset.topicPartition()} " +
                        "offset=${offset.offset()} " +
                        "timestamp=${dateFormat.format(Date(record.timestamp()))}" +
                        "key=${record.key()} value=${record.value()}"
            )
            offset.acknowledge()
            latch.countDown()
        }
    }

    companion object {
        private const val BOOTSTRAP_SERVERS = "localhost:9092"
        private const val TOPIC = "demo-topic"

        fun main(args: Array<String>) {
            val count = 10
            val latch = CountDownLatch(count)
            val consumer = SampleConsumer(BOOTSTRAP_SERVERS)
            val disposable = consumer.consumeMessages(TOPIC, latch)
            latch.await(10, TimeUnit.SECONDS)
            disposable.dispose()
        }
    }

    init {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
        props[ConsumerConfig.CLIENT_ID_CONFIG] = "sample-consumer"
        props[ConsumerConfig.GROUP_ID_CONFIG] = "sample-group"
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = IntegerDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.AUTO_OFFSET_RESET_CONFIG] = "earliest"
        receiverOptions = ReceiverOptions.create(props)
        dateFormat = SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy")
    }
}
