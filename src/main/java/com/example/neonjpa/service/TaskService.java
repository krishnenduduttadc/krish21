
package com.example.neonjpa.service;

import com.example.neonjpa.dto.TaskRequest;
import com.example.neonjpa.dto.TaskResponse;
import com.example.neonjpa.entity.Task;
import com.example.neonjpa.repository.TaskRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public TaskResponse create(TaskRequest request) {
        Task task = new Task(request.title(), request.description());

        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }

        return TaskResponse.from(repository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id) {
        return TaskResponse.from(getTask(id));
    }

    public TaskResponse update(Long id, TaskRequest request) {
        Task task = getTask(id);

        task.setTitle(request.title());
        task.setDescription(request.description());

        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }

        return TaskResponse.from(repository.save(task));
    }

    public void delete(Long id) {
        repository.delete(getTask(id));
    }

    private Task getTask(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Task not found: " + id
            ));
    }
}
