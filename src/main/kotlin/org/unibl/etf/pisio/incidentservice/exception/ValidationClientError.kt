package org.unibl.etf.pisio.incidentservice.exception

class ValidationClientError(type: ClientErrorType, message: String, val fieldErrors: List<FieldError>) :
    ClientError(type, message)