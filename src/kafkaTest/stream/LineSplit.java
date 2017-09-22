package kafkaTest.stream;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

public class LineSplit {
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		final KStreamBuilder builder = new KStreamBuilder();

		KStream<String, String> source = builder.stream("streams-plaintext-input");
		source.flatMapValues(value -> Arrays.asList(value.split("\\W+"))).to("streams-linesplit-output");

		final KafkaStreams streams = new KafkaStreams(builder, props);
		final CountDownLatch latch = new CountDownLatch(1);

		// ... same as Pipe.java below
		Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
			@Override
			public void run() {
				streams.close();
				latch.countDown();
			}
		});

		try {
			streams.start();
			latch.await();
		} catch (Throwable e) {
			System.exit(1);
		}
		System.exit(0);
	}
}
