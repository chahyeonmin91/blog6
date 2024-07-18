package com.example.blog6.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {



    @GetMapping("/")
    public String home(Model model) {
        return "main";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    @GetMapping("/userregform")
    public String userRegForm() {
        return "userregform";
    }

    @GetMapping("/userreg")
    public String userReg() {
        return "userreg";
    }

    @GetMapping("/userreg_error")
    public String userRegError() {
        return "userreg_error";
    }
}