package org.unibl.etf.pisio.incidentservice.extensions

import org.unibl.etf.pisio.incidentservice.incident.*
import org.unibl.etf.pisio.incidentservice.type.Type
import org.unibl.etf.pisio.incidentservice.type.TypeResponse
import java.time.Clock
import java.time.OffsetDateTime

fun Type.toTypeResponse() = TypeResponse(id!!, name)

fun Incident.toIncidentResponse() =
    IncidentResponse(id!!, description, imageUrl, longitude, latitude, createdAt, updatedAt, status, type)

fun CreateIncidentRequest.toIncident(type: Type, imageResponse: ImageResponse, clock: Clock) = Incident(
    null,
    description!!,
    imageResponse.url,
    imageResponse.id,
    longitude!!,
    latitude!!,
    OffsetDateTime.now(clock),
    OffsetDateTime.now(clock),
    Status.PENDING,
    type
)

