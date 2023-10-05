package org.unibl.etf.pisio.incidentservice.type

import kotlinx.coroutines.flow.Flow
import java.util.*

interface TypeRepository {

    suspend fun findById(id: UUID): Type?

    fun findAllByParentIsNull(): Flow<Type>

    fun findAllByParent(parent: Type): Flow<Type>
}