package ar.edu.uncuyo.dashboard.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@Controller
public class HomeController {
    @GetMapping("/")
    public String page() {
        return "/public/index";
    }

    @GetMapping("/public/**")
    public String page(Principal principal, HttpServletRequest request) {
        String pageName = request.getRequestURI().substring(1);

        if (pageName.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return pageName;
    }
}
