package com.minicurso_java.todolist.task;

import com.minicurso_java.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private TaskRepository taskRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        taskModel.setIdUser((UUID) request.getAttribute("idUser"));

        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getFinishAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start/finish date must be bigger than current date");
        }

        if (taskModel.getStartAt().isAfter(taskModel.getFinishAt())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Finish date must be bigger than start date");
        }

        var task = this.taskRepository.save(taskModel);
        return ResponseEntity.status(200).body(task);
    }

    @GetMapping("/")
    public List<TaskModel> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskRepository.findAllByIdUser((UUID) idUser);
        return tasks;
    }

    @PatchMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, HttpServletRequest request, @PathVariable UUID id) {
        var task = this.taskRepository.findById(id).orElse(null);

        // Validando se a task existe
        if (task == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Task not found");
        }

        // Validando se o usuário é dono da tarefa que ele está tentando alterar
        var idUser = request.getAttribute("idUser");
        if (!task.getIdUser().equals(idUser)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("User not allowed to update this task");
        }

        Utils.copyNonNullProperties(taskModel, task);

        return ResponseEntity.ok().body(this.taskRepository.save(task));
    }
}
