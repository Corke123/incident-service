package org.unibl.etf.pisio.incidentservice.map

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.util.*

@Service
class MapService(@Qualifier("mapServiceClient") private val mapServiceClient: WebClient) {

    suspend fun getIncidentIdsWithinRadius(
        longitude: Double?, latitude: Double?, radius: Double?
    ) = mapServiceClient.get().uri {
            it.queryParam("longitude", "{longitude}")
                .queryParam("latitude", "{latitude}")
                .queryParam("radius", "{radius}")
                .build(longitude, latitude, radius)
        }.retrieve().awaitBody<List<UUID>>()

}