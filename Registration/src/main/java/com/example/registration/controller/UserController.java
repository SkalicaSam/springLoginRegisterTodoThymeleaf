package com.example.registration.controller;

import com.example.registration.model.User;
import com.example.registration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;


@Controller
public class UserController {

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @Autowired
    UserRepository userRepository;

    @GetMapping("/register")
    public String GetRegisterPage(@ModelAttribute("user") User user){

        return "register";
    }

    @PostMapping ("/register")
    public String SaveUser(@ModelAttribute("user") User user, Model model){
        boolean userExists = userRepository.existsByUsername(user.getUsername());

        if (userExists) {
            model.addAttribute("error", "Užívateľ s týmto používateľským menom už existuje.");
            return "register";
        } else {
            // Hashovanie hesla pred uložením do databázy
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);

            user.setRoles("ADMIN");
            userRepository.save(user);
            model.addAttribute("message", "Submitted succesfuly");
            return "register";
        }
    }

    @GetMapping("/users")
    public String usersPage(Model model){
        List<User> listOfUsers = userRepository.findAll();
        model.addAttribute("users", listOfUsers);
        //System.out.println(userRepository.findAll());
        return "users";
        // return "Greetings from Spring Boot!";
    }

    @GetMapping("/login")
    public String Login(){
        return "login";
    }

    @GetMapping("/hello")// Greeting.. Sign Out
    public String Hello(){
        return "hello";
    }

}
