package com.example.registration.controller;

import com.example.registration.model.Task;
import com.example.registration.model.User;
import com.example.registration.repository.TaskRepository;
import com.example.registration.repository.UserRepository;
import com.example.registration.service.CustomUserDetailsService;
import com.example.registration.service.TaskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.SessionAttributes;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/tasks")
@SessionAttributes("userLoggedInSession")
//@SessionAttributes("userLoggedInLogPageSession")

public class TaskController {
    private final TaskService taskService;
    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    @Autowired
    private TaskRepository taskRepository; // Assuming TaskRepository is your repository interface
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @ModelAttribute("userLoggedInSession")
    public User getLoggedInUser() {
        return customUserDetailsService.getLoggedInUser();
    }

    @GetMapping //("/tasks")*
    public String getAllTasks(Model model ) {
        User userLoggedIn= customUserDetailsService.getLoggedInUser();
        if (userLoggedIn == null){
        model.addAttribute("message", "No userLoggedIn.");
        return "alltasks";
        }

        List<Task> tasks = taskService.getTasksByUser(userLoggedIn);
        if (tasks.isEmpty()) {
            model.addAttribute("message", "No tasks available.");
        } else {
            model.addAttribute("tasks", tasks);
        }
        return "alltasks";
    }

    @GetMapping("/{id}")
    public String getTaskById(@PathVariable Long id, Model model) {
        Task task = taskService.getTaskForLoggedInUserById(id);
        if (task == null) {
            return "error/403";
        }
        model.addAttribute("task", task);
        return "task-details";
    }

    @GetMapping("/create")
    public String showTaskForm(Model model, @ModelAttribute("userLoggedInSession") User userLoggedInSession) {
        if (userLoggedInSession == null){
            model.addAttribute("message", "No userLoggedIn.");
            return "redirect:/tasks";
        }
        model.addAttribute("task", new Task());
        return "task-create";
    }

    @PostMapping("/saveTask")
    public String saveTask(@ModelAttribute("task") Task task, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "redirect:/tasks/create";
        }
        task.setCreatedAt(LocalDateTime.now());
        User userLoggedIn = customUserDetailsService.getLoggedInUser();
        if (userLoggedIn != null) {
            task.setUser(userLoggedIn);
            taskRepository.save(task);
            redirectAttributes.addFlashAttribute("successMessage", "Task created successfully!");
            return "redirect:/tasks";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to create task. Please try again.");
            return "redirect:/tasks/";
        }
    }

    @GetMapping("/edit/{taskId}")
    public String showEditTaskForm(@PathVariable("taskId") Long taskId, Model model, @ModelAttribute("userLoggedInSession") User userLoggedInSession) {
        if (userLoggedInSession == null){
            model.addAttribute("message", "No userLoggedIn.");
            return "redirect:/tasks";
        }
        Optional<Task> taskOptional = taskService.getTaskByTaskIdAndUserLoggedInIdForLoggedInUserById(taskId, userLoggedInSession);
        if (taskOptional.isPresent()) {
            model.addAttribute("task", taskOptional.get());
            return "task-edit";
        } else {
            return "redirect:/tasks";
        }
    }

//    @GetMapping("/edit2/{id}")
//    public String showEditTaskForm2(@PathVariable("id") Long id, Model model) {
//        Optional<Task> taskOptional = taskRepository.findById(id);
//        if (taskOptional.isPresent()) {
//            model.addAttribute("task", taskOptional.get());
//            return "task-edit";
//        } else {
//            return "redirect:/tasks";
//        }
//    }

    @PostMapping("/updateTask/{id}")
    public String updateTask(@PathVariable("id") Long id, @ModelAttribute("task") Task task) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username);
        if (user != null) {
            task.setUser(user);
        }
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);
        return "redirect:/tasks";
    }

//    @PostMapping("/deleteTask/{id}")
//@RequestMapping(value="/deleteTask/{id}", method={RequestMethod.DELETE, RequestMethod.GET})

@RequestMapping(value="/deleteTask/{id}", method={RequestMethod.DELETE, RequestMethod.GET})
    public String deleteTask(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        User userLoggedIn = customUserDetailsService.getLoggedInUser();
        if (userLoggedIn == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete task. Please Login and try again.");
            return "redirect:/tasks";
        }
        Task taskToDelete = taskService.getTaskById(id);
        if (taskToDelete == null){
            redirectAttributes.addFlashAttribute("errorMessage", "Task not exist");
            return "redirect:/tasks";
        }
        if(taskToDelete.getUser() != userLoggedIn){
            redirectAttributes.addFlashAttribute("errorMessage", "User has no privileges to delete task");
            return "redirect:/tasks";
        }
        taskService.deleteTask(id);
        redirectAttributes.addFlashAttribute("successMessage", "Task deleted successfully!");
        return "redirect:/tasks";
    }
}

//UnIntent = shift + tab;

//        find userLoggedInLogPageSession from LogPage, but not working when the server is restarting.
//        It Wont hold userLoggedInLogPageSession saved in session. Solution: Ukladanie session do databázy (JDBC);
//
//        ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
//        HttpSession session = attr.getRequest().getSession(true);
//        User userLoggedInLogPageSession = (User) session.getAttribute("userLoggedInLogPageSession");
//
//        if (userLoggedInLogPageSession != null){
//         System.out.println("userLoggedInLogPageSession getId ===  " + userLoggedInLogPageSession.getId());
//        }else{
//            System.out.println("userLoggedInLogPageSession getId === null;; " + userLoggedInLogPageSession );
//        }

// System.out.println("MyComponent inidsadtialized.");


//        if (userLoggedInSession != null){
//         System.out.println("userLoggedInLogPageSession getId ===  " + userLoggedInSession.getId());
//        }else{
//            System.out.println("userLoggedInLogPageSession getId === nu;; " + userLoggedInSession );
//
//        }


// po restarte serveru uzivatel sice ostane prihlaseny, ale vyhodi mi uzivatela == null.

//            User userLogged = customUserDetailsService.getUserLoggedInFromLogPageSession();
//            if (userLogged != null) {
//                System.out.println("userLoggedInLogPageSession getId ===  " + userLogged.getId());
//            }
// po restarte serveru uzivatel sice ostane prihlaseny, ale vyhodi mi uzivatela == null.
