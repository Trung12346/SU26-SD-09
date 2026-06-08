package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.service.UserService;

@Controller
@RequestMapping("/admin/nguoi-dung")
public class AdminNguoiDungController {

    @Autowired
    private UserService userService;

    @GetMapping
    public String index(
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model
    ) {
        NguoiDung nguoiDung = new NguoiDung();
        nguoiDung.setTrangThai(true);
        loadFormAndList(model, nguoiDung, keyword, "Them nguoi dung");
        return "admin/nguoi-dung-list";
    }

    @GetMapping("/edit/{id}")
    public String edit(
            @PathVariable("id") int id,
            @RequestParam(name = "keyword", defaultValue = "") String keyword,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        NguoiDung nguoiDung = userService.Getbyid(id);
        if (nguoiDung == null) {
            redirectAttributes.addFlashAttribute("error", "Khong tim thay nguoi dung");
            return "redirect:/admin/nguoi-dung";
        }

        loadFormAndList(model, nguoiDung, keyword, "Cap nhat nguoi dung");
        return "admin/nguoi-dung-list";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute NguoiDung nguoiDung,
            @RequestParam(name = "vaiTroId") Integer vaiTroId,
            @RequestParam(name = "matKhauMoi", required = false) String matKhauMoi,
            RedirectAttributes redirectAttributes
    ) {
        try {
            userService.saveAdmin(nguoiDung, vaiTroId, matKhauMoi);
            redirectAttributes.addFlashAttribute("success", "Luu nguoi dung thanh cong");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/admin/nguoi-dung";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        userService.setTrangThai(id, false);
        redirectAttributes.addFlashAttribute("success", "Khoa nguoi dung thanh cong");
        return "redirect:/admin/nguoi-dung";
    }

    private void loadFormAndList(Model model, NguoiDung nguoiDung, String keyword, String title) {
        model.addAttribute("nguoiDung", nguoiDung);
        model.addAttribute("nguoiDungs", userService.search(keyword));
        model.addAttribute("vaiTros", userService.findAllVaiTro());
        model.addAttribute("keyword", keyword);
        model.addAttribute("title", title);
    }
}
