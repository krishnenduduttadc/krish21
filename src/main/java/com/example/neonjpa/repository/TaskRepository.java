package com.example.neonjpa.repository;

import com.example.neonjpa.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findAllByOwner_Id(Long ownerId, Pageable pageable);

    Optional<Task> findByIdAndOwner_Id(Long id, Long ownerId);
}