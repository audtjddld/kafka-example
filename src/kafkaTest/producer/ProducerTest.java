package kafkaTest.producer;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.internals.DefaultPartitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.PartitionInfo;

public class ProducerTest implements Runnable {

	private int producerId;

	public ProducerTest(int number) {
		this.producerId = number;
	}

	public static void main(String[] args) throws Exception {
		int count = 2;
		ExecutorService executor = Executors.newFixedThreadPool(count);
		for (int i = 0; i < count; i++) {
			executor.submit(new ProducerTest(i));
		}

	}

	public Properties producerProperties() {
		Properties props = new Properties();
		
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker : localhost:9092");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		// props.put("value.serializer",
		// "org.apache.kafka.common.serialization.ByteArraySerializer");
		props.put("request.required.acks", "-1");
		props.put("partitioner.class", SimplePartitional.class);

		// props.put("partitioner.class", "path.to.custom.partitioner.class");
		return props;
	}

	@Override
	public void run() {
		System.out.println("producer Run ");
		KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties());

		int i = 0;
		while (true) {
			producer.send(new ProducerRecord<String, String>("test2", "oauthToken" + (i++), "aaaa" + (i++)));
			try {
				TimeUnit.SECONDS.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getProducerId() {
		return producerId;
	}

	public void setProducerId(int producerId) {
		this.producerId = producerId;
	}

	public static class SimplePartitional extends DefaultPartitioner {

		private int num;

		@Override
		public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
			List<PartitionInfo> partitions = cluster.partitionsForTopic(topic);
			return (num+++1) % partitions.size();
		}

	}
}
