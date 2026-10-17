package com.example.neonjpa.controller;

import com.example.neonjpa.dto.TaskRequest;
import com.example.neonjpa.dto.TaskResponse;
import com.example.neonjpa.service.TaskService;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService service;

    public TaskController(TaskService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        return service.create(request, authentication.getName());
    }

    @GetMapping
    public Page<TaskResponse> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if (page < 0 || size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "page must be >= 0 and size must be between 1 and 100"
            );
        }

        PageRequest pageable = PageRequest.of(
                page, size, Sort.by(Sort.Direction.DESC, "id")
        );

        return service.findAll(pageable, authentication.getName());
    }

    @GetMapping("/{id}")
    public TaskResponse findById(
            @PathVariable Long id,
            Authentication authentication) {
        return service.findById(id, authentication.getName());
    }

    @PutMapping("/{id}")
    public TaskResponse update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        return service.update(id, request, authentication.getName());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id,
            Authentication authentication) {
        service.delete(id, authentication.getName());
    }
}