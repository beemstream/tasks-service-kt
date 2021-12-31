package task.service

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.NoHandlerFoundException
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver
import java.util.*

data class RestApiError(val requestId: String, val errorCodes: List<String>, val errorType: String = "request_invalid")

@ControllerAdvice
class ErrorHandler : ExceptionHandlerExceptionResolver() {

  fun generateUuid(): String = "cs_${UUID.randomUUID()}"

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValid(ex: MethodArgumentNotValidException): ResponseEntity<RestApiError> {
    val errors = ex.fieldErrors.map {
      val codes = Optional.ofNullable(it.codes)
      when (codes.isPresent && codes.get().contains("NotNull") || codes.get().contains("NotEmpty")) {
        true -> "${it.field}_required"
        false -> "${it.field}_invalid"
      }
    }
    logger.info("validation error: $errors")
    return validationError(errors)
  }

  @ExceptionHandler(MismatchedInputException::class)
  fun handleMismatchException(ex: MismatchedInputException): ResponseEntity<RestApiError> =
    validationError(getFieldErrors(ex, "invalid"))

  @ExceptionHandler(MissingKotlinParameterException::class)
  fun handleKotlinException(ex: MissingKotlinParameterException): ResponseEntity<RestApiError> =
    validationError(getFieldErrors(ex, "required"))

  @ExceptionHandler(JsonParseException::class)
  fun handleMessageNotReadableException(ex: JsonParseException): ResponseEntity<RestApiError> {
    logger.info("json error: $ex")
    return validationError(listOf("json_invalid"))
  }

  @ExceptionHandler(ResponseStatusException::class)
  fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<String> {
    logger.info("response status exception: $ex")
    return ResponseEntity(null, ex.status)
  }

  @ExceptionHandler(NoHandlerFoundException::class)
  fun handleResponseStatusException(ex: NoHandlerFoundException): ResponseEntity<String> {
    return ResponseEntity(null, HttpStatus.NOT_FOUND)
  }

  private fun getFieldErrors(ex: MismatchedInputException, errorSuffix: String): List<String> {
    val errors = ex.path.map { "${it.fieldName}_${errorSuffix}" }
    logger.info("validation error: $errors")
    return errors
  }

  private fun validationError(errors: List<String>) =
    ResponseEntity(RestApiError(generateUuid(), errors), HttpStatus.BAD_REQUEST)
}