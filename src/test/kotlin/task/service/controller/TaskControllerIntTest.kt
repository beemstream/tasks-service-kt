package task.service.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import task.service.models.Task
import task.service.repository.TaskRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import task.service.TestUtils
import java.util.*

@ExtendWith(SpringExtension::class)
@WebMvcTest(TaskController::class)
class TaskControllerIntTest {

  @Autowired
  lateinit var mvc: MockMvc

  @MockBean
  lateinit var repository: TaskRepository

  @MockBean
  lateinit var mongo: MongoTemplate

  val mapper = jacksonObjectMapper()

  @Test
  fun shouldSuccessfullyGetAllTasks() {
    val storedTasks = listOf(Task(name = "foo", done = false, UUID.randomUUID().toString()))
    `when`(repository.findAll()).thenReturn(storedTasks)

    val request = MockMvcRequestBuilders.get("/api/tasks")
    val result = mvc.perform(request).andReturn()

    val tasksResult: List<Task> = mapper.readValue(result.response.contentAsString)

    assert(result.response.status == 200)
    assert(tasksResult == storedTasks)
  }

  @Test
  fun shouldSuccessfullyPostTasks() {
    val model = Task("Task 1", true, UUID.randomUUID().toString())
    `when`(repository.save(model)).thenReturn(model)

    val request = MockMvcRequestBuilders.post("/api/tasks")
      .accept(MediaType.APPLICATION_JSON)
      .contentType(MediaType.APPLICATION_JSON)
      .content(TestUtils.asJsonString(model))

    val result = mvc.perform(request).andReturn()

    val tasksResult: Task = mapper.readValue(result.response.contentAsString)

    assert(result.response.status == 201)
    assert(tasksResult == model)
  }
}