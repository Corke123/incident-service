package org.unibl.etf.pisio.incidentservice.type

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import java.util.*

data class Type(@Id val id: UUID?, val name: String, @Column("parent_id") val parent: Type?)
