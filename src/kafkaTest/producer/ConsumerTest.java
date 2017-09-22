package kafkaTest.producer;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

public class ConsumerTest implements Runnable {
	private int consumerNumber;
	public ConsumerTest(int number) {
		this.consumerNumber = number;
	}
	
	
	public int getConsumerNumber() {
		return consumerNumber;
	}


	public void setConsumerNumber(int consumerNumber) {
		this.consumerNumber = consumerNumber;
	}


	public Properties consumerProps() {
		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "broker : localhost:9092");	// kafka
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "tester");	// group id
		//props.put("auto.commit.interval.ms", "1000");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
		//props.put("session.timeout.ms", "10000");
		//props.put("partition.assignment.strategy", "org.apache.kafka.clients.consumer.RoundRobinAssignor");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		//props.put("value.deserializer", "org.apache.kafka.common.serialization.ByteArrayDeserializer");
		return props;
	}
	
	public static void main(String[] args) {
		int numConsumers = 2;
		ExecutorService executor = Executors.newFixedThreadPool(numConsumers);
		
		for(int i = 0 ; i < numConsumers ; i++) {
			executor.submit(new ConsumerTest(i));
		}
	}

	@Override
	public void run() {
		System.out.println("consumer run~");
		KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProps());
		consumer.subscribe(Arrays.asList("test2"));
		
		/**/
		while (true) {
			ConsumerRecords<String, String> records = consumer.poll(100);
			for (ConsumerRecord<String, String> record : records) {
				//TODO packet process
				System.out.println("this consumer number " + this.consumerNumber + " > " + record.toString());
			}
			consumer.commitSync();
			//consumer.close();
		}
		/**/
		
	}
}
