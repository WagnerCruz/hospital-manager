package com.raidstack.config;

import com.raidstack.kafka.events.AgendamentoEvent;
import com.raidstack.kafka.events.UsuarioEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.consumers.usuario.group-id}")
    private String usuarioConsumerGroupId;

    @Value("${app.kafka.consumers.agendamento.group-id}")
    private String agendamentoConsumerGroupId;

    public <T> ConsumerFactory<String, T> consumerFactory(String groupID, Class<T> targetType) {

        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupID);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        JacksonJsonDeserializer<T> deserializer = new JacksonJsonDeserializer<>(targetType);
        deserializer.addTrustedPackages("com.raidstack.kafka.events");

        return new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UsuarioEvent> usuarioCriadoKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UsuarioEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory(usuarioConsumerGroupId, UsuarioEvent.class));

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, UsuarioEvent> usuarioAtualizadoKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, UsuarioEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory(usuarioConsumerGroupId, UsuarioEvent.class));

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AgendamentoEvent> agendamentoCriadoKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AgendamentoEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory(agendamentoConsumerGroupId, AgendamentoEvent.class));

        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, AgendamentoEvent> agendamentoAtualizadoKafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, AgendamentoEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory(agendamentoConsumerGroupId, AgendamentoEvent.class));

        return factory;
    }
}