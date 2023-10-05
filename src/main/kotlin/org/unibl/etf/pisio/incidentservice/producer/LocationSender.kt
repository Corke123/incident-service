package org.unibl.etf.pisio.incidentservice.producer

import org.springframework.amqp.core.Exchange
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component
import org.unibl.etf.pisio.incidentservice.config.RabbitMQProperties
import java.util.*

@Component
class LocationSender(val template: RabbitTemplate, val exchange: Exchange, val rabbitMQProperties: RabbitMQProperties) {

    suspend fun send(location: Location) {
        template.convertAndSend(exchange.name, rabbitMQProperties.routingKey, location)
    }
}

data class Location(val id: UUID, val longitude: Double, val latitude: Double)