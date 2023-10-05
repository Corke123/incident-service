package org.unibl.etf.pisio.incidentservice.exception

import mu.two.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.MissingRequestValueException
import org.springframework.security.access.AccessDeniedException

private val logger = KotlinLogging.logger {}

private const val EXCEPTION_DURING_API_CALL = "Exception during api call"

@ControllerAdvice
class ApiExceptionHandler {

    @ExceptionHandler(WebExchangeBindException::class)
    fun handle(exception: WebExchangeBindException): ResponseEntity<ValidationClientError> {
        val fieldErrors = exception.bindingResult.fieldErrors.map {
            val fieldErrorType =
                if ("NotNull" == it.code || "NotBlank" == it.code || "NotEmpty" == it.code) FieldErrorType.REQUIRED else FieldErrorType.INVALID
            val objectName = it.objectName
            val field = it.field
            val defaultMessage = it.defaultMessage ?: "Error with field"
            FieldError(fieldErrorType, objectName, field, defaultMessage)
        }
        val errorType = ClientErrorType.VALIDATION_ERROR
        val validationClientError = ValidationClientError(errorType, errorType.message, fieldErrors)
        logger.error(EXCEPTION_DURING_API_CALL, exception)
        return ResponseEntity.status(errorType.httpStatus).body(validationClientError)
    }

    @ExceptionHandler(TypeNotFoundException::class)
    fun typeNotFoundException(exception: TypeNotFoundException): ResponseEntity<ClientError> {
        val errorType = ClientErrorType.VALIDATION_ERROR
        val clientError = ClientError(errorType, errorType.message)
        logger.error(EXCEPTION_DURING_API_CALL, exception)
        return ResponseEntity.status(errorType.httpStatus).body(clientError)
    }

    @ExceptionHandler(MissingRequestValueException::class)
    fun missingRequestValueException(exception: MissingRequestValueException): ResponseEntity<ClientError> {
        val errorType = ClientErrorType.VALIDATION_ERROR
        val clientError = ClientError(errorType, exception.message)
        logger.error(EXCEPTION_DURING_API_CALL, exception)
        return ResponseEntity.status(errorType.httpStatus).body(clientError)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun accessDeniedException(exception: AccessDeniedException): ResponseEntity<ClientError> {
        val errorType = ClientErrorType.ACCESS_DENIED
        val clientError = ClientError(errorType, errorType.message)
        logger.error(EXCEPTION_DURING_API_CALL, exception)
        return ResponseEntity.status(errorType.httpStatus).body(clientError)
    }

    @ExceptionHandler(Exception::class)
    fun exception(exception: java.lang.Exception): ResponseEntity<Any> {
        val errorType = ClientErrorType.UNEXPECTED_ERROR
        val clientError = ClientError(errorType, errorType.message)
        logger.error(EXCEPTION_DURING_API_CALL, exception)
        return ResponseEntity.status(errorType.httpStatus).body(clientError)
    }
}