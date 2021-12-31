package task.service

import com.fasterxml.jackson.databind.ObjectMapper
import java.lang.Exception
import java.lang.RuntimeException

object TestUtils {
  fun asJsonString(obj: Any?): String {
    return try {
      val mapper = ObjectMapper()
      mapper.writeValueAsString(obj)
    } catch (e: Exception) {
      throw RuntimeException(e)
    }
  }
}