package com.example.registration.repository;

import com.example.registration.model.Task;
import com.example.registration.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface  TaskRepository extends JpaRepository<Task, Long> {
    //List<Task> findByUsername(String username);
//    @EntityGraph(attributePaths = "files")
//    @Query("SELECT t FROM Task t WHERE t.user.id = :userId")
    List<Task> findByUser(User user);
    //List<Task>getAllTasks();

    List<Task> findByUserId(Long UserId);
    Page<Task> findByUserId(Long userId, Pageable pageable);
    Page<Task> findByUserIdAndDescriptionContaining(Long userId, String description, Pageable pageable);
    Page<Task> findByUserIdAndTitleContaining(Long userId, String title, Pageable pageable);

    Page<Task> findByUserIdAndId(Long userId, Long id, Pageable pageable);







}
