package task.service.configuration

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StringDeserializer
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.text.MessageFormat

@Configuration
class ValidatorConfiguration {
  fun stringDeserializer(): SimpleModule {
    val stringDeserializerModule = SimpleModule()
    stringDeserializerModule.addDeserializer(String::class.javaObjectType, CoercionLessStringDeserializer())
    return stringDeserializerModule
  }

  @Bean
  @Primary
  fun objectMapper(): ObjectMapper {
    return ObjectMapper()
      .registerModule(KotlinModule())
      .registerModule(stringDeserializer())
      .configure(MapperFeature.ALLOW_COERCION_OF_SCALARS, false)
  }
}

class CoercionLessStringDeserializer : StringDeserializer() {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): String {
    val forbiddenTypes =
      listOf(
        JsonToken.VALUE_NUMBER_INT,
        JsonToken.VALUE_NUMBER_FLOAT,
        JsonToken.VALUE_TRUE,
        JsonToken.VALUE_FALSE
      )

    if (forbiddenTypes.contains(p.currentToken)) {
      val message = MessageFormat.format("Cannot coerce {0} to String value", p.currentToken)
      throw MismatchedInputException.from(p, String::class.javaObjectType, message)
    }
    return super.deserialize(p, ctxt)
  }
}