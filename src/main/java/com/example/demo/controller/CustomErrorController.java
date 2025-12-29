package com.example.demo.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object error = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAttribute("statusCode", statusCode);
            
            String errorMessage = "An unexpected error occurred";
            switch (statusCode) {
                case 400:
                    errorMessage = "Bad Request - Your request is invalid";
                    break;
                case 401:
                    errorMessage = "Unauthorized - You are not authorized to access this page";
                    break;
                case 403:
                    errorMessage = "Forbidden - You do not have permission to access this page";
                    break;
                case 404:
                    errorMessage = "Not Found - The requested resource could not be found";
                    break;
                case 500:
                    errorMessage = "Internal Server Error - Something went wrong on our server";
                    break;
                case 503:
                    errorMessage = "Service Unavailable - The server is temporarily unavailable";
                    break;
                default:
                    if (error != null) {
                        errorMessage = error.toString();
                    }
            }
            
            model.addAttribute("errorMessage", errorMessage);
        }
        
        return "error";
    }
}