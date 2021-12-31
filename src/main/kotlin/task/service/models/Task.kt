package task.service.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Document
data class Task(
  @field:Valid @field:NotEmpty val name: String,
  @field:Valid @field:NotNull val done: Boolean?,
  @Id val id: String?
)