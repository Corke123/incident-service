package org.unibl.etf.pisio.incidentservice.exception

import org.springframework.http.HttpStatus

open class ClientError(val type: ClientErrorType, val message: String)

enum class ClientErrorType(val httpStatus: HttpStatus, val message: String) {
    VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_ENTITY, "There are validation errors in the request"),
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "Forbidden — you don't have permission to access this resource")
}