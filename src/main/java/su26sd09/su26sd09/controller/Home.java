package su26sd09.su26sd09.controller;

import jakarta.servlet.http.HttpSession;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.service.CustomerUserDetailsService;

import java.security.Principal;

@RequestMapping("/home")
@Controller
public class Home {

     @Autowired
    CustomerUserDetailsService repo;
    @Autowired
    NguoiDungRepository UserRepo;

    @GetMapping("")
    public String home(){
        return "index";
    }
}
