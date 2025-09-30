package ar.edu.uncuyo.dashboard.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class UsuarioController {
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginForm", "");
        return "/login";
    }
}
