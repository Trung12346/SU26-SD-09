package su26sd09.su26sd09.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Nhanvien;
import su26sd09.su26sd09.service.NhanVienService;
import su26sd09.su26sd09.service.UserService;

import java.security.Principal;

@Controller
@RequestMapping("/admin/nhan-vien")
public class AdminNhanVienController {

    @Autowired
    NhanVienService repo;
    @Autowired
    UserService NguoiDungRepo;

    public Boolean CheckRole(String email){
        String role = "";

        for (NguoiDung n : NguoiDungRepo.getAll()){
            if(n.getEmail().equals(email)) {
                if (n.getVaiTro().getTenVaiTro().equals("ROLE_ADMIN")) {
                    role = n.getVaiTro().getTenVaiTro();
                }
            }
        }

        if(role == null || role.isEmpty() || !role.equals("ROLE_ADMIN")){
            return false;
        }
        if(role.equals("ROLE_ADMIN")){
            return true;
        }
        return false;
    }


    @GetMapping
    public String index(Model model){
        model.addAttribute("nhanViens",repo.findAll());
        model.addAttribute("nhanVien",new Nhanvien());
        model.addAttribute("nguoiDungs",NguoiDungRepo.getAll());
        return "admin/nhan-vien-list";
    }


    @PostMapping("delete/{id}")
    public String deleteNhanVien(Principal P, @PathVariable("id") int id){
        if (CheckRole(P.getName())){
            repo.delete(repo.findbyid(id));
        }
        return "redirect:/admin/nhan-vien";
    }


    @PostMapping("/save")
    public String saveNhanVien(@Valid Nhanvien nv, BindingResult r, Principal p, RedirectAttributes redirect){
     if(CheckRole(p.getName())){

         if(nv.n.getVaiTro().getTenVaiTro().equals("ROLE_ADMIN") || nv.n.getVaiTro().getTenVaiTro().equals("ROLE_EMPLOYEE")){
             redirect.addFlashAttribute("error","người dùng không hợp lệ (role Admin) hoặc (employee)");
             return "redirect:/admin/nhan-vien";
         }

         else if(nv.n.isTrangThai() == false ){
             redirect.addFlashAttribute("error","người dùng không hợp lệ (tài khoản đã bị khóa)");
             return "redirect:/admin/nhan-vien";
         }

         repo.save(nv);
         redirect.addFlashAttribute("success","thêm nhân viên thành công");
     }

        return "redirect:/admin/nhan-vien";
    }

    @GetMapping("/edit/{id}")
    public String editNhanVien(Model model,Principal p,@PathVariable("id") int id){
        if(CheckRole(p.getName())){
            model.addAttribute("nhanViens",repo.findAll());
            model.addAttribute("nhanVien",repo.findbyid(id));
            model.addAttribute("nguoiDungs",NguoiDungRepo.getAll());
            return "admin/nhan-vien-list";
        }
        return "redirect:/home";
    }

    @GetMapping("/search")
    public String searchNhanVien(Model model, Principal p ,@RequestParam("name") String name , RedirectAttributes redirect){
        if (CheckRole(p.getName())){
            model.addAttribute("nhanViens",repo.findByName(name));
            model.addAttribute("nhanVien",new Nhanvien());
            model.addAttribute("nguoiDungs",NguoiDungRepo.getAll());
            redirect.addFlashAttribute("success","tổng số tìm được = " + repo.findByName(name).size());
            return "admin/nhan-vien-list";
        }
        return "redirect:/admin/nhan-vien";
    }
}
