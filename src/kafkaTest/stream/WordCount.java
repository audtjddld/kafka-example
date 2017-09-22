package kafkaTest.stream;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KStreamBuilder;

public class WordCount {
	/*
	 * source.to("stream-pipe-output") 토픽에 쓰는 방법.
	 * 
	 * 스트림 생성부에 작성도 가능하다.
	 * builder.stream("streams-plaintext-input").to("streams-pipe-output")
	 * 
	 * 
	 * KafkaStreams streams = new KafkaStreams(builder, props)
	 */
	public static void main(String[] args) throws Exception {
		Properties props = new Properties();
		props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount");
		props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9094");
		props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
		props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

		final KStreamBuilder builder = new KStreamBuilder();

		KStream<String, String> source = builder.stream("streams-plaintext-input");
		source.flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split("\\W+")))
				.groupBy((key, value) -> value).count("Counts")
				.to(Serdes.String(), Serdes.Long(), "streams-wordcount-output");

		final KafkaStreams streams = new KafkaStreams(builder, props);
		final CountDownLatch latch = new CountDownLatch(1);

		// ... same as Pipe.java below
	}

}
