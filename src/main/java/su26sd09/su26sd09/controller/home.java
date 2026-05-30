package su26sd09.su26sd09.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/home")
@Controller
public class home {

    @GetMapping("")
    public String home(){
        return "index";
    }
}
