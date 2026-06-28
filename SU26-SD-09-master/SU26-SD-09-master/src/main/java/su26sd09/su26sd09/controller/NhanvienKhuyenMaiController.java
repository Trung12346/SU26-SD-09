package su26sd09.su26sd09.controller;


import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.KhuyenMai;
import su26sd09.su26sd09.service.NguoiDungService;
import su26sd09.su26sd09.service.khuyenMaiService;

import javax.naming.Binding;
import java.security.Principal;


@Controller
@RequestMapping("/Nhan-vien/khuyen-mai")
public class NhanvienKhuyenMaiController {

    @Autowired
    khuyenMaiService repo;

    @Autowired
    NguoiDungService UserRepo;


    @GetMapping
    public String index(Model model, Principal p){
        model.addAttribute("khuyenMai",new KhuyenMai());
        model.addAttribute("khuyenMais",repo.findAll());
        return "nhan-vien/khuyen-mai-list";
    }


    @GetMapping("/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id){
        model.addAttribute("khuyenMai",repo.findbyId(id));
        model.addAttribute("khuyenMais",repo.findAll());
        return "nhan-vien/khuyen-mai-list";
    }


    @PostMapping("/save")
    public String save(@Valid KhuyenMai m , BindingResult r, Principal p, RedirectAttributes redirect){
        if (!r.hasErrors()){
            for(KhuyenMai km : repo.findAll()){
                if (km.getPromoCode().equalsIgnoreCase(m.promoCode) && km.getId() != m.id){
                    redirect.addFlashAttribute("error","mã khuyến mãi này đã tồn tại vui lòng nhập tên khác");
                    return "redirect:/Nhan-vien/khuyen-mai";
                }
            }
            if (m.ngayBatDau.isAfter(m.ngayKetThuc) || m.ngayKetThuc.equals(m.ngayBatDau)){
                redirect.addAttribute("error","ngày bắt đầu phải bắt đầu sau ngày kết thúc ít nhất 1 ngày");
                return "redirect:/Nhan-vien/khuyen-mai";
            }

            if (m.n == null){
                m.setN(UserRepo.findByEmail(p.getName()));
            }
            repo.save(m);
        }else{
            redirect.addAttribute("error",r.getFieldError().getDefaultMessage());
            return "redirect:/Nhan-vien/khuyen-mai";
        }
        return "redirect:/Nhan-vien/khuyen-mai";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id){
        repo.delete(repo.findbyId(id));
        return "redirect:/Nhan-vien/khuyen-mai";
    }

    @GetMapping("/search")
    public String search(Model model,@RequestParam("keyword") String keyword){
        model.addAttribute("khuyenMai",new KhuyenMai());
        model.addAttribute("khuyenMais",repo.findbyNameVoucher(keyword));
        return "Nhan-vien/khuyen-mai-list";
    }

}
