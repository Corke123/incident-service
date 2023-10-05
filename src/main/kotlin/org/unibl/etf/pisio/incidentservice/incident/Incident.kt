package org.unibl.etf.pisio.incidentservice.incident

import org.springframework.data.annotation.Id
import org.unibl.etf.pisio.incidentservice.type.Type
import java.time.OffsetDateTime
import java.util.*

data class Incident(
    @Id var id: UUID?,
    val description: String,
    val imageUrl: String,
    val imageId: UUID,
    val longitude: Double,
    val latitude: Double,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val status: Status,
    val type: Type
)

enum class Status {
    PENDING, APPROVED, DELETED
}
