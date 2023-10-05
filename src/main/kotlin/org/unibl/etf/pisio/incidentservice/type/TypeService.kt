package org.unibl.etf.pisio.incidentservice.type

import org.springframework.stereotype.Service
import java.util.UUID

@Service
class TypeService(val typeRepository: TypeRepository) {

    suspend fun getById(id: UUID) = typeRepository.findById(id)

    fun getRootTypes() = typeRepository.findAllByParentIsNull()

    fun getSubtypes(type: Type) = typeRepository.findAllByParent(type)
}