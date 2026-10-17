package com.example.neonjpa.service;

import com.example.neonjpa.dto.TaskRequest;
import com.example.neonjpa.dto.TaskResponse;
import com.example.neonjpa.entity.AppUser;
import com.example.neonjpa.entity.Task;
import com.example.neonjpa.entity.TaskPriority;
import com.example.neonjpa.repository.AppUserRepository;
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

    private final TaskRepository taskRepository;
    private final AppUserRepository userRepository;

    public TaskService(
            TaskRepository taskRepository,
            AppUserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse create(TaskRequest request, String email) {
        AppUser owner = getUser(email);

        Task task = new Task(request.title(), request.description());
        task.setOwner(owner);

        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }

        task.setPriority(
                request.priority() != null
                        ? request.priority()
                        : TaskPriority.MEDIUM
        );
        task.setDueDate(request.dueDate());

        return TaskResponse.from(taskRepository.save(task));
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> findAll(
            Pageable pageable, String email) {
        AppUser owner = getUser(email);

        return taskRepository
                .findAllByOwner_Id(owner.getId(), pageable)
                .map(TaskResponse::from);
    }

    @Transactional(readOnly = true)
    public TaskResponse findById(Long id, String email) {
        AppUser owner = getUser(email);
        return TaskResponse.from(getTask(id, owner.getId()));
    }

    public TaskResponse update(
            Long id, TaskRequest request, String email) {
        AppUser owner = getUser(email);
        Task task = getTask(id, owner.getId());

        task.setTitle(request.title());
        task.setDescription(request.description());

        if (request.completed() != null) {
            task.setCompleted(request.completed());
        }

        if (request.priority() != null) {
            task.setPriority(request.priority());
        }

        // A missing/null due date clears the due date.
        task.setDueDate(request.dueDate());

        return TaskResponse.from(taskRepository.save(task));
    }

    public void delete(Long id, String email) {
        AppUser owner = getUser(email);
        Task task = getTask(id, owner.getId());
        taskRepository.delete(task);
    }

    private AppUser getUser(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Authenticated user not found"
                ));
    }

    private Task getTask(Long id, Long ownerId) {
        return taskRepository.findByIdAndOwner_Id(id, ownerId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Task not found"
                ));
    }
}