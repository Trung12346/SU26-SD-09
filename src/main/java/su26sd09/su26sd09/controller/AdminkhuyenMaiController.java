package su26sd09.su26sd09.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.KhuyenMai;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.service.khuyenMaiService;

import java.security.Principal;

@Controller
@RequestMapping("/admin/khuyen-mai")
public class AdminkhuyenMaiController {

    @Autowired
    NguoiDungRepository NguoiDungrepo;

    public Boolean CheckRole(String email){
        String role = "";

        for (NguoiDung n : NguoiDungrepo.findAll()){
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

    @Autowired
    khuyenMaiService repo;


    @GetMapping
    public String index(Model model){
        model.addAttribute("khuyenMais",repo.findAll());
        model.addAttribute("khuyenMai",new KhuyenMai());
        model.addAttribute("nguoiDungs",NguoiDungrepo.findAll());
        return "admin/khuyen-mai-list";
    }


    @PostMapping("/delete/{id}")
    public  String deleteKhuyenMai(@PathVariable("id") int id, Principal p){
      if (CheckRole(p.getName())){
          repo.delete(repo.findbyId(id));

      }
        else{
            return "redirect:/admin/khuyen-mai";
      }
    return "redirect:/admin/khuyen-mai";
    }


    @PostMapping("/save")
    public String saveKhuyenMai(RedirectAttributes redirect,Model model, Principal p, @ModelAttribute("khuyenMai") KhuyenMai m, BindingResult r){
        if(CheckRole(p.getName())){
               if(r.hasErrors() || m.ngayBatDau == null || m.ngayKetThuc == null ){
                    redirect.addFlashAttribute("error","vui lòng xem lại trường dữ liệu");
                    return "redirect:/admin/khuyen-mai";
               }

               else if(m.ngayKetThuc.isBefore(m.ngayBatDau) || m.ngayKetThuc.equals(m.ngayBatDau)){
                    redirect.addFlashAttribute("error","ngày kết thúc không phải sau ngày bắt đầu ít nhất 1 ngày");
                  return "redirect:/admin/khuyen-mai";
               }

                if(m.id == 0){
                   redirect.addFlashAttribute("success","Luu khuyen mai thanh cong");

                }if(m.id != 0){
                    redirect.addFlashAttribute("success","Cap nhat khuyen mai thanh cong");
            }
               repo.save(m);

            System.out.println(m.n.getEmail());
        }
        return "redirect:/admin/khuyen-mai";
    }


    @GetMapping("/edit/{id}")
    public String updateKhuyenMai(Principal p,Model model,@PathVariable("id") int id){
      if(CheckRole(p.getName())){
       model.addAttribute("khuyenMai",repo.findbyId(id));
       model.addAttribute("khuyenMais",repo.findAll());
       model.addAttribute("nguoiDungs",NguoiDungrepo.findAll());
       return "admin/khuyen-mai-list";
    }
    return "redirect:/admin/khuyen-mai";
    }

    @GetMapping("/search")
    public String search(Principal p ,Model model,@RequestParam("keyword") String keyword){
     if(CheckRole(p.getName())){
         model.addAttribute("khuyenMais", repo.findbyNameVoucher(keyword));
         model.addAttribute("khuyenMai",new KhuyenMai());
         model.addAttribute("nguoiDungs",NguoiDungrepo.findAll());
         return "admin/khuyen-mai-list";
     }

     return "redirect:/admin/khuyen-mai";
    }
}
