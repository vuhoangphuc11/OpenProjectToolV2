package com.example.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class MyErrorController implements ErrorController {
    @GetMapping(value = "/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("message", "Page Not Found");
                return "error";
            } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("message", "Internal server error");
                return "error";
            } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("message", "Unauthorized");
                return "error";
            } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("message", "Forbidden");
                return "error";
            }
        }
        return "error";
    }
}
