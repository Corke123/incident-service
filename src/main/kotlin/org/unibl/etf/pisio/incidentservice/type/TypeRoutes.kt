package org.unibl.etf.pisio.incidentservice.type

import kotlinx.coroutines.flow.map
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.*
import org.unibl.etf.pisio.incidentservice.extensions.toTypeResponse
import java.util.*

@Component
class TypeRoutes {

    @Bean
    fun httpTypes(typeService: TypeService) = coRouter {
        "/api/v1/types".nest {
            GET("") {
                ServerResponse.ok().bodyAndAwait(typeService.getRootTypes().map { it.toTypeResponse() })
            }
            GET("/{typeId}/subtypes") { serverRequest ->
                val id = UUID.fromString(serverRequest.pathVariable("typeId"))
                val type = typeService.getById(id)
                if (type != null) {
                    val result = typeService.getSubtypes(type).map { it.toTypeResponse() }
                    ServerResponse.ok().bodyAndAwait(result)
                } else {
                    ServerResponse.notFound().buildAndAwait()
                }
            }
        }
    }
}

data class TypeResponse(val id: UUID, val name: String)
