package org.unibl.etf.pisio.incidentservice.incident

import jakarta.validation.Valid
import jakarta.validation.constraints.*
import kotlinx.coroutines.flow.map
import mu.two.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import org.unibl.etf.pisio.incidentservice.extensions.toIncidentResponse
import org.unibl.etf.pisio.incidentservice.type.Type
import java.net.URI
import java.time.OffsetDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@RestController
@RequestMapping("/api/v1/incidents")
class IncidentController(val incidentService: IncidentService) {

    @GetMapping
    suspend fun getIncidents(
        @RequestParam(value = "lng", required = false) longitude: Double?,
        @RequestParam(value = "lat", required = false) latitude: Double?,
        @RequestParam(value = "radius", required = false) radius: Double?,
        @RequestParam(value = "period", required = false) period: Long?,
        @RequestParam(value = "type", required = false) type: UUID?,
        @RequestParam(value = "status", required = false) statuses: List<Status>?
    ) = incidentService.findAllFiltered(longitude, latitude, radius, period, type, statuses)
        .map { it.toIncidentResponse() }

    @GetMapping("/{id}")
    suspend fun getById(@PathVariable id: UUID): ResponseEntity<IncidentResponse> {
        val incident = incidentService.findById(id)
        return if (incident == null) {
            ResponseEntity.notFound().build()
        } else {
            ResponseEntity.ok(incident.toIncidentResponse())
        }
    }

    @PostMapping
    suspend fun createIncident(
        @RequestPart("incident") @Valid @NotNull createIncidentRequest: CreateIncidentRequest,
        @RequestPart("image") @Valid @NotNull image: FilePart
    ): ResponseEntity<*> {
        logger.info("Attempt to create new incident")
        val createdImage = incidentService.createIncident(createIncidentRequest, image)
        return ResponseEntity.created(URI.create("/api/v1/incidents/${createdImage.id}")).build<IncidentResponse>()
    }

    @PatchMapping("/{id}")
    suspend fun updateIncident(
        @PathVariable id: UUID,
        @RequestBody @Valid @NotNull updateIncidentRequest: UpdateIncidentRequest
    ): ResponseEntity<IncidentResponse> {
        val savedIncident = incidentService.findById(id)
        return if (savedIncident != null) {
            ResponseEntity.ok(
                incidentService.updateIncidentStatus(savedIncident, updateIncidentRequest.status).toIncidentResponse()
            )
        } else {
            ResponseEntity.notFound().build()
        }
    }

}

data class CreateIncidentRequest(
    @field:NotBlank(message = "Description is required")
    @field:Size(max = 2048, message = "Maximum length for description is 2048 characters")
    val description: String?,

    @field:NotNull(message = "Longitude is required")
    @field:Min(value = -90, message = "Minimum value for longitude is -90 degrees")
    @field:Max(value = 90, message = "Maximum value for longitude is 90 degrees")
    val longitude: Double?,

    @field:NotNull(message = "Latitude is required")
    @field:Min(value = -180, message = "Minimum value for latitude is -180 degrees")
    @field:Max(value = 180, message = "Maximum value for latitude is 180 degrees")
    val latitude: Double?,

    @field:NotNull(message = "Type is required")
    val typeId: UUID?
)

data class UpdateIncidentRequest(val status: Status)

data class IncidentResponse(
    val id: UUID,
    val description: String,
    val imageUrl: String,
    val longitude: Double,
    val latitude: Double,
    val createdAt: OffsetDateTime,
    val updatedAt: OffsetDateTime,
    val status: Status,
    val type: Type
)
