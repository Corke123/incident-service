package org.unibl.etf.pisio.incidentservice.incident

import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime
import java.util.*

interface IncidentRepository {

    suspend fun findById(id: UUID): Incident?
    fun findAllByIdsInAndStatusesInAndTypeAndAfterDate(
        ids: List<UUID>,
        statuses: List<Status>,
        typeId: UUID?,
        afterTime: OffsetDateTime?
    ): Flow<Incident>

    suspend fun save(incident: Incident): Incident
}