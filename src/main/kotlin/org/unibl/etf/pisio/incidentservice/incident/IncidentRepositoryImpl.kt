package org.unibl.etf.pisio.incidentservice.incident

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import org.unibl.etf.pisio.incidentservice.type.Type
import reactor.core.publisher.Mono
import java.lang.StringBuilder
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

private const val INSERT_QUERY = """
    INSERT INTO incident (description, image_id, image_url, longitude, latitude, created_at, updated_at, status, type_id)
    VALUES (:description, :image_id, :image_url, :longitude, :latitude, :created_at, :updated_at, :status, :type_id)
"""

private const val SELECT_QUERY = """
    SELECT i.id i_id, i.description i_description, i.image_id i_image_id, i.image_url i_image_url, 
        i.longitude i_longitude, i.latitude i_latitude, i.created_at i_created_at, i.updated_at i_updated_at, 
        i.status i_status, t.id t_id, t.name t_name, p.id p_id, p.name p_name 
    FROM incident i
    INNER JOIN type t ON i.type_id = t.id
    LEFT JOIN type p ON t.parent_id = p.id
    WHERE 1 = 1"""

private const val UPDATE_QUERY = """
    UPDATE incident 
    SET description = :description, 
        image_id = :image_id, 
        image_url = :image_url, 
        longitude = :longitude, 
        latitude = :latitude, 
        created_at = :created_at, 
        updated_at = :updated_at, 
        status = :status, 
        type_id = :type_id
    WHERE id = :id
"""

@Repository
class IncidentRepositoryImpl(val databaseClient: DatabaseClient) :
    IncidentRepository {

    override suspend fun findById(id: UUID): Incident? {
        return databaseClient.sql("$SELECT_QUERY AND i.id = :id")
            .bind("id", id)
            .fetch()
            .all()
            .bufferUntilChanged{it["i_id"]}
            .flatMap { incidentFromRows(it) }
            .awaitSingle()
    }

    override fun findAllByIdsInAndStatusesInAndTypeAndAfterDate(
        ids: List<UUID>,
        statuses: List<Status>,
        typeId: UUID?,
        afterTime: OffsetDateTime?
    ): Flow<Incident> {
        val sql = StringBuilder(SELECT_QUERY)
        if (ids.isNotEmpty()) sql.append(" AND i.id IN (:ids)")
        if (statuses.isNotEmpty()) sql.append(" AND i.status IN (:statuses)")
        typeId?.let { sql.append(" AND (t.id = :typeId OR p.id = :typeId)") }
        afterTime?.let { sql.append(" AND i.created_at > :afterDate") }

        var query = databaseClient.sql(sql.toString())

        if (ids.isNotEmpty()) query = query.bind("ids", ids)
        if (statuses.isNotEmpty()) query = query.bind("statuses", statuses.map { it.name })
        typeId?.let { query = query.bind("typeId", typeId) }
        afterTime?.let { query = query.bind("afterDate", afterTime) }

        return query.fetch()
            .all()
            .bufferUntilChanged { it["i_id"] }
            .flatMap { incidentFromRows(it) }
            .asFlow()
    }

    override suspend fun save(incident: Incident): Incident {
        return if (incident.id == null) {
            databaseClient.sql(INSERT_QUERY)
                .bind("description", incident.description)
                .bind("image_id", incident.imageId)
                .bind("image_url", incident.imageUrl)
                .bind("longitude", incident.longitude)
                .bind("latitude", incident.latitude)
                .bind("created_at", incident.createdAt)
                .bind("updated_at", incident.updatedAt)
                .bind("status", incident.status.name)
                .bind("type_id", incident.type.id!!)
                .filter { statement -> statement.returnGeneratedValues("id") }
                .fetch().first()
                .doOnNext { incident.id = UUID.fromString(it["id"].toString()) }
                .thenReturn(incident)
                .awaitSingle()
        } else {
            this.databaseClient.sql(UPDATE_QUERY)
                .bind("description", incident.description)
                .bind("image_id", incident.imageId)
                .bind("image_url", incident.imageUrl)
                .bind("longitude", incident.longitude)
                .bind("latitude", incident.latitude)
                .bind("created_at", incident.createdAt)
                .bind("updated_at", incident.updatedAt)
                .bind("status", incident.status.name)
                .bind("type_id", incident.type.id!!)
                .bind("id", incident.id!!)
                .fetch()
                .first()
                .thenReturn(incident)
                .awaitSingle()
        }
    }

    private fun incidentFromRows(rows: List<Map<String, Any>>): Mono<Incident> {
        val id = rows[0]["i_id"] as UUID
        val description = rows[0]["i_description"] as String
        val imageId = rows[0]["i_image_id"] as UUID
        val imageUrl = rows[0]["i_image_url"] as String
        val longitude = (rows[0]["i_longitude"] as BigDecimal).toDouble()
        val latitude = (rows[0]["i_latitude"] as BigDecimal).toDouble()
        val createdAt = rows[0]["i_created_at"] as OffsetDateTime
        val updatedAt = rows[0]["i_updated_at"] as OffsetDateTime
        val status = Status.valueOf(rows[0]["i_status"] as String)
        val parent = if (rows[0]["p_id"] != null)
            Type(rows[0]["p_id"] as UUID, rows[0]["p_name"] as String, null) else null
        val type = Type(rows[0]["t_id"] as UUID, rows[0]["t_name"] as String, parent)

        val incident = Incident(
            id, description, imageUrl, imageId, longitude, latitude, createdAt, updatedAt, status, type
        )
        return Mono.just(incident)

    }
}
