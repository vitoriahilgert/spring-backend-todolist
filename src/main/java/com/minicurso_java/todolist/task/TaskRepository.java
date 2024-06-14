package com.minicurso_java.todolist.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<TaskModel, UUID> {
    List<TaskModel> findAllByIdUser(UUID idUser);

    TaskModel findByIdAndIdUser(UUID id, UUID idUser);
}
