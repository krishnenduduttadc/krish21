
package com.example.neonjpa.dto;

import com.example.neonjpa.entity.Task;
import com.example.neonjpa.entity.TaskPriority;
import java.time.Instant;
import java.time.LocalDate;

public record TaskResponse(
    Long id,
    String title,
    String description,
    boolean completed,
    Instant createdAt,
    TaskPriority priority,
    LocalDate dueDate
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.isCompleted(),
            task.getCreatedAt(),
            task.getPriority(),
            task.getDueDate()
        );
    }
}
