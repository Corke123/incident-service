package org.unibl.etf.pisio.incidentservice.type

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import java.util.*

private const val SELECT_BY_ID = """
    SELECT t.id t_id, t.name t_name, p.id p_id, p.name p_name FROM type t 
    LEFT OUTER JOIN type p ON t.parent_id = p.id
    WHERE t.id = :id
    """

private const val SELECT_ALL_BY_PARENT_IS_NULL = """
    SELECT t.id t_id, t.name t_name FROM type t WHERE t.parent_id IS NULL
    """

private const val SELECT_ALL_BY_PARENT = """
    SELECT t.id t_id, t.name t_name, p.id p_id, p.name p_name FROM type t 
    INNER JOIN type p on t.parent_id = p.id
    WHERE p.id = :id
    """

@Repository
class TypeRepositoryImpl(val databaseClient: DatabaseClient) : TypeRepository {
    override suspend fun findById(id: UUID): Type? {
        return databaseClient.sql(SELECT_BY_ID)
            .bind("id", id)
            .fetch()
            .all()
            .bufferUntilChanged { it["t_id"] }
            .flatMap { typeFromRows(it) }
            .awaitSingle()
    }

    override fun findAllByParentIsNull(): Flow<Type> {
        return databaseClient.sql(SELECT_ALL_BY_PARENT_IS_NULL)
            .fetch()
            .all()
            .bufferUntilChanged { it["t_id"] }
            .flatMap { typeFromRows(it) }
            .asFlow()
    }

    override fun findAllByParent(parent: Type): Flow<Type> {
        return databaseClient.sql(SELECT_ALL_BY_PARENT)
            .bind("id", parent.id!!)
            .fetch()
            .all()
            .bufferUntilChanged { it["t_id"] }
            .flatMap { typeFromRows(it) }
            .asFlow()
    }

    private fun typeFromRows(rows: List<Map<String, Any>>): Mono<Type> {
        val id = rows[0]["t_id"] as UUID
        val name = rows[0]["t_name"] as String
        val parent = if (rows[0]["p_id"] != null) {
            val parentId = rows[0]["p_id"] as UUID
            val parentName = rows[0]["p_name"] as String
            Type(parentId, parentName, null)
        } else {
            null
        }
        return Mono.just(Type(id, name, parent))
    }
}