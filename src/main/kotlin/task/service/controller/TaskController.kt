package task.service.controller

import org.jetbrains.annotations.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import task.service.logger
import task.service.models.Task
import task.service.repository.TaskRepository
import javax.validation.Valid

@RestController
@RequestMapping("/api/tasks")
class TaskController(private val taskRepo: TaskRepository) {
  val logger = logger()

  @GetMapping
  fun getAllTasks(): ResponseEntity<List<Task>> {
    val allTasks = taskRepo.findAll()
    logger.info("getting {} tasks", allTasks.size)

    return ResponseEntity(allTasks, HttpStatus.OK)
  }

  @GetMapping("/{id}")
  fun getTask(@PathVariable @NotNull id: String): ResponseEntity<Task> {
    logger.info("getting task from id {}", id)
    val task = taskRepo.findById(id)

    when (task.isPresent) {
      true -> {
        logger.info("getting task {}", task)
        return ResponseEntity(task.get(), HttpStatus.OK)
      }
      false -> throw ResponseStatusException(HttpStatus.NOT_FOUND)
    }
  }

  @PostMapping
  fun createTasks(@RequestBody @Valid request: Task): ResponseEntity<Task> {
    logger.info("creating new task {}", request)

    return ResponseEntity(taskRepo.save(request), HttpStatus.CREATED)
  }
}
