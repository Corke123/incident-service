package org.unibl.etf.pisio.incidentservice.incident

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.two.KotlinLogging
import org.springframework.http.codec.multipart.FilePart
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.unibl.etf.pisio.incidentservice.exception.TypeNotFoundException
import org.unibl.etf.pisio.incidentservice.extensions.toIncident
import org.unibl.etf.pisio.incidentservice.image.ImageService
import org.unibl.etf.pisio.incidentservice.map.MapService
import org.unibl.etf.pisio.incidentservice.producer.Location
import org.unibl.etf.pisio.incidentservice.producer.LocationSender
import org.unibl.etf.pisio.incidentservice.type.TypeService
import reactor.core.publisher.Mono
import java.time.Clock
import java.time.OffsetDateTime
import java.util.*

private val logger = KotlinLogging.logger {}

@Service
class IncidentService(
    private val locationSender: LocationSender,
    private val incidentRepository: IncidentRepository,
    private val typeService: TypeService,
    private val imageService: ImageService,
    private val mapService: MapService,
    private val clock: Clock
) {

    @Transactional(readOnly = true)
    suspend fun findById(id: UUID) = incidentRepository.findById(id)

    @Transactional(readOnly = true)
    suspend fun findAllFiltered(
        longitude: Double?,
        latitude: Double?,
        radius: Double?,
        period: Long?,
        type: UUID?,
        statuses: List<Status>?
    ): Flow<Incident> {
        logger.info("Fetch incidents")
        val locationIdsInRadius = mapService.getIncidentIdsWithinRadius(longitude, latitude, radius)
        val authorities: MutableCollection<out GrantedAuthority>? = getUserAuthorities()
        val afterTime = period?.let { OffsetDateTime.now(clock).minusDays(period) }
        return if (authorities != null && isModerator(authorities)) {
            incidentRepository.findAllByIdsInAndStatusesInAndTypeAndAfterDate(
                locationIdsInRadius,
                statuses ?: emptyList(),
                type,
                afterTime
            )
        } else {
            incidentRepository.findAllByIdsInAndStatusesInAndTypeAndAfterDate(
                locationIdsInRadius,
                listOf(Status.APPROVED),
                type,
                afterTime
            )
        }
    }

    @Transactional
    suspend fun createIncident(createIncidentRequest: CreateIncidentRequest, image: FilePart): Incident {
        logger.info("Saving new incident")
        val type = typeService.getById(createIncidentRequest.typeId!!)
            ?: throw TypeNotFoundException("There is not type with id: ${createIncidentRequest.typeId}")
        val imageResponse = imageService.uploadImage(image)
        val incident = createIncidentRequest.toIncident(type, imageResponse, clock)
        val createdIncident = incidentRepository.save(incident)
        locationSender.send(Location(createdIncident.id!!, createdIncident.longitude, createdIncident.latitude))
        return createdIncident
    }

    @Transactional
    suspend fun updateIncidentStatus(incident: Incident, status: Status): Incident {
        logger.info("Set image as $status")
        val updatedIncident = incident.copy(status = status, updatedAt = OffsetDateTime.now(clock))
        return incidentRepository.save(updatedIncident)
    }

    private suspend fun getUserAuthorities() = ReactiveSecurityContextHolder.getContext().flatMap {
        Mono.just(it.authentication.authorities)
    }.awaitSingleOrNull()

    private fun isModerator(authorities: MutableCollection<out GrantedAuthority>) =
        authorities.mapNotNull { it.authority }.contains("ROLE_MODERATOR")

}

data class ImageResponse(val id: UUID, val url: String)

