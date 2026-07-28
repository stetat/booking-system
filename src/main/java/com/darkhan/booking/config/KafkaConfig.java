package com.darkhan.booking.config;

import com.darkhan.booking.booking.BookingTopics;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Bean
    NewTopic bookingConfirmedTopic() {
        return TopicBuilder.name(BookingTopics.BOOKING_CONFIRMED)
                .partitions(3)
                .replicas(1)
                .build();

    }
}
