package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

    @GetMapping(value = "/login/form")
    public String loginForm() {
        return "login";
    }

    @RequestMapping(value = "/login/success")
    public String success(Model model) {
        return "redirect:/home";
    }

    @RequestMapping(value = "/login/error")
    public String error(Model model) {
        model.addAttribute("message", "Username or Password entered wrong!");
        return "login";
    }

    @RequestMapping(value = "/unauthoried")
    public String Unauthoried(Model model) {
        model.addAttribute("message", "You can not access this page!");
        return "error";
    }

    @RequestMapping(value = "/logout/success")
    public String logoutSuccess(Model model) {
        model.addAttribute("message", "Logout success");
        return "login";
    }

}
