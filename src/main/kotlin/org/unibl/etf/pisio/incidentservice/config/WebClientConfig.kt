package org.unibl.etf.pisio.incidentservice.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
@EnableConfigurationProperties(value = [ImageProcessorProperties::class, MapServiceProperties::class])
class WebClientConfig {

    @Bean(name = ["imageProcessorClient"])
    fun imageProcessorClient(imageProcessorProperties: ImageProcessorProperties) =
        WebClient.create("${imageProcessorProperties.baseUrl}/api/v1/images")

    @Bean(name = ["mapServiceClient"])
    fun mapServiceClient(mapServiceProperties: MapServiceProperties) =
        WebClient.create("${mapServiceProperties.baseUrl}/api/v1/locations")
}

@ConfigurationProperties(prefix = "incident-service.image-processor")
data class ImageProcessorProperties(val baseUrl: String)

@ConfigurationProperties(prefix = "incident-service.map-service")
data class MapServiceProperties(val baseUrl: String)