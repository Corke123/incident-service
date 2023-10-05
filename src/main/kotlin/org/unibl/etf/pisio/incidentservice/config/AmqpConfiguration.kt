package org.unibl.etf.pisio.incidentservice.config

import org.springframework.amqp.core.*
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(RabbitMQProperties::class)
class AmqpConfiguration {

    @Bean
    fun queue(rabbitMQProperties: RabbitMQProperties) = Queue(rabbitMQProperties.queue)

    @Bean
    fun exchange(rabbitMQProperties: RabbitMQProperties): Exchange =
        ExchangeBuilder.directExchange(rabbitMQProperties.exchange).build()

    @Bean
    fun binding(queue: Queue, exchange: Exchange, rabbitMQProperties: RabbitMQProperties): Binding =
        BindingBuilder.bind(queue).to(exchange).with(rabbitMQProperties.routingKey).noargs()

    @Bean
    fun producerJackson2MessageConverter() = Jackson2JsonMessageConverter()
}

@ConfigurationProperties(prefix = "incident-service.rabbitmq")
data class RabbitMQProperties(val queue: String, val exchange: String, val routingKey: String)