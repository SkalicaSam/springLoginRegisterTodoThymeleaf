package com.example.registration.service;

import com.example.registration.model.Task;
import com.example.registration.model.User;
import com.example.registration.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static java.util.Locale.filter;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;
//    private final TaskRepository taskRepository;


    @Autowired
    private CustomUserDetailsService customUserDetailsService;

//    @Autowired
//    public TaskService(TaskRepository taskRepository) {
//        this.taskRepository = taskRepository;
//    }

    // Metódy na manipuláciu s úlohami

//    public List<Task> getTasksByUsername(String username) {
//        return taskRepository.findByUsername(username);
//    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public List<Task> getTasksByUser(User user) {
        return taskRepository.findByUser(user);
    }

    public Page<Task> findByUserId(Long userId, Pageable pageable) {
        return taskRepository.findByUserId(userId, pageable);
    }

    public Task getTaskForLoggedInUserById(Long id) {
        User userLoggedIn = customUserDetailsService.getLoggedInUser();

        return taskRepository.findById(id)
                .filter(task -> task.getUser().equals(userLoggedIn))
                .orElse(null);
    }

    public Optional<Task> getTaskByTaskIdAndUserLoggedInIdForLoggedInUserById(Long Id, User userLoggedIn ) {
        //User userLoggedIn = customUserDetailsService.getLoggedInUser();
        System.out.println(" 777 tasks id=" +  Id);
        //User userLoggedInId = userLoggedIn;
        System.out.println(" 888 userLoggedInId=" +  userLoggedIn.getId());

        return Optional.ofNullable(taskRepository.findById(Id)
                .filter(task -> task.getUser().getId().equals(userLoggedIn.getId()))
                .orElse(null));
    }

    public Task getTaskById(Long id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {
         taskRepository.deleteById(id);
    }


}
