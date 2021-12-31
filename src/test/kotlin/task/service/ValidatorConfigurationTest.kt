package task.service

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import task.service.configuration.ValidatorConfiguration
import task.service.models.Task
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ValidatorConfigurationTest {

  @Test
  fun disallowsIntToStringCoercion() {
    val mapper = ValidatorConfiguration().objectMapper()
    val exception: MismatchedInputException =
      assertThrows { mapper.readValue("{ \"name\": 1234 }", Task::class.javaObjectType) }

    assert(exception.message!!.contains("Cannot coerce VALUE_NUMBER_INT to String value"))
  }

  @Test
  fun disallowsFloatToStringCoercion() {
    val mapper = ValidatorConfiguration().objectMapper()
    val exception: MismatchedInputException =
      assertThrows { mapper.readValue("{ \"name\": 1.2 }", Task::class.javaObjectType) }

    assert(exception.message!!.contains("Cannot coerce VALUE_NUMBER_FLOAT to String value"))
  }

  @Test
  fun disallowsTrueToStringCoercion() {
    val mapper = ValidatorConfiguration().objectMapper()
    val exception: MismatchedInputException = assertThrows {
      mapper.readValue(
        "{ \"name\": true }",
        Task::class.javaObjectType
      )
    }

    assert(exception.message!!.contains("Cannot coerce VALUE_TRUE to String value"))
  }

  @Test
  fun disallowsFalseToStringCoercion() {
    val mapper = ValidatorConfiguration().objectMapper()
    val exception: MismatchedInputException =
      assertThrows { mapper.readValue("{ \"name\": false }", Task::class.javaObjectType) }

    assert(exception.message!!.contains("Cannot coerce VALUE_FALSE to String value"))
  }
}
