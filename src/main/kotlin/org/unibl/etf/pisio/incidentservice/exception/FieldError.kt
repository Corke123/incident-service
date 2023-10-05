package org.unibl.etf.pisio.incidentservice.exception

data class FieldError(val type: FieldErrorType, val `object`: String, val field: String, val message: String)

enum class FieldErrorType {
    REQUIRED, INVALID
}