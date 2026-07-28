package com.darkhan.booking.booking;

import com.darkhan.booking.TestcontainersConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.kafka.autoconfigure.KafkaConnectionDetails;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.testcontainers.kafka.KafkaContainer;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Import(TestcontainersConfiguration.class)
public class KafkaConnectionTest {

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    KafkaConnectionDetails kafkaConnectionDetails;

    @Autowired
    KafkaContainer kafkaContainer;

    @Test
    void kafkaNotNull() {
        assertThat(kafkaTemplate).isNotNull();
    }

    @Test
    void kafkaDetailsMatch() {
        assertThat(kafkaConnectionDetails.getBootstrapServers())
                .containsExactly(kafkaContainer.getBootstrapServers());
    }


}
