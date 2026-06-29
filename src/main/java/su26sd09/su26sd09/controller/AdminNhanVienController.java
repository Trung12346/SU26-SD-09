package su26sd09.su26sd09.controller;

import jakarta.validation.Valid;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Nhanvien;
import su26sd09.su26sd09.repository.NhanVienRepo;
import su26sd09.su26sd09.repository.VaiTroRepo;
import su26sd09.su26sd09.service.NhanVienService;
import su26sd09.su26sd09.service.UserService;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin/nhan-vien")
public class AdminNhanVienController {

    @Autowired
    NhanVienService repo;
    @Autowired
    UserService NguoiDungRepo;
    @Autowired
    VaiTroRepo vaiTroRepo;
    @Autowired
    NhanVienRepo nvrepo;



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
        Nhanvien nv = new Nhanvien();
        nv.setN(new NguoiDung());
        model.addAttribute("nhanViens",repo.findAll());
        model.addAttribute("nhanVien",nv);
        model.addAttribute("vaiTros",vaiTroRepo.findAll());

        model.addAttribute("nguoiDungs",repo.ListAdd());
        return "admin/nhan-vien-list";
    }


    @PostMapping("/lock/{id}")
    public String deleteNhanVien(Principal P, @PathVariable("id") int id,RedirectAttributes redirect){


        repo.lock(repo.findbyid(id));
        redirect.addFlashAttribute("success","khóa nhân viên thành công");


        return "redirect:/admin/nhan-vien";

    }


    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/save")
    public String saveNhanVien(@Valid Nhanvien nv, BindingResult bindingResult,
                               Principal principal, RedirectAttributes redirect,
                               @RequestParam(value = "matKhaumoi", required = false) String matKhauMoi,
                               @RequestParam(value = "maNguoiDung", required = false) Integer maNguoiDung) {

        if (!CheckRole(principal.getName())) {
            return "redirect:/admin/nhan-vien";
        }

        boolean isNew = nv.getId() == 0;

        if (isNew && maNguoiDung != null) {
            return themNhanVienTuNguoiDungCoSan(nv, maNguoiDung, redirect);
        }

        for (FieldError fe : bindingResult.getFieldErrors()) {
            if (fe.getField().equals("matKhau_hash") && matKhauMoi != null && !matKhauMoi.isBlank()) {
                nv.getN().setMatKhau_hash(passwordEncoder.encode(matKhauMoi));
            } else {
                redirect.addFlashAttribute("error", fe.getDefaultMessage());
                return "redirect:/admin/nhan-vien";
            }
        }

        if (isNew) {
            return themNhanVienMoi(nv, redirect);
        }

        if (maNguoiDung != null) {
            return capNhatNhanVien(nv, maNguoiDung, matKhauMoi, redirect);
        }

        redirect.addFlashAttribute("error", "vui lòng chọn nhân viên");
        return "redirect:/admin/nhan-vien";
    }


    private String themNhanVienTuNguoiDungCoSan(Nhanvien nv, Integer maNguoiDung,
                                                RedirectAttributes redirect) {
        if (repo.IsNhanVienTonTai(maNguoiDung)) {
            redirect.addFlashAttribute("error", "nhân viên này đã tồn tại");
            return "redirect:/admin/nhan-vien";
        }
        if (nv.getBoPhan() == null || nv.getBoPhan().isBlank()) {
            redirect.addFlashAttribute("error", "bộ phận không được để trống");
            return "redirect:/admin/nhan-vien";
        }
        nv.setN(NguoiDungRepo.Getbyid(maNguoiDung));
        repo.save(nv);
        redirect.addFlashAttribute("success", "thêm nhân viên thành công");
        return "redirect:/admin/nhan-vien";
    }

    private String themNhanVienMoi(Nhanvien nv, RedirectAttributes redirect) {
        if (nv.getBoPhan() == null || nv.getBoPhan().isBlank()) {
            return "redirect:/admin/nhan-vien";
        }
        if (NguoiDungRepo.checkEmail(nv.getN().getEmail(), nv.getN().getMaNguoiDung())) {
            redirect.addFlashAttribute("error", "email đã tồn tại");
            return "redirect:/admin/nhan-vien";
        }
        NguoiDungRepo.save(nv.getN());
        repo.save(nv);
        redirect.addFlashAttribute("success", "thêm nhân viên thành công");
        return "redirect:/admin/nhan-vien";
    }

    private String capNhatNhanVien(Nhanvien nv, Integer maNguoiDung,
                                   String matKhauMoi, RedirectAttributes redirect) {
        if (repo.TrungNv(maNguoiDung, nv.getId())) {
            redirect.addFlashAttribute("error", "vui lòng chọn mã nhân viên không trùng với nhân viên khác");
            return "redirect:/admin/nhan-vien";
        }

        NguoiDung nguoiDung = NguoiDungRepo.Getbyid(maNguoiDung);
        String oldEmail = nguoiDung.getEmail();

        nguoiDung.setNgayCapNhat(LocalDateTime.now());
        nguoiDung.setHoTen(nv.getN().getHoTen());
        nguoiDung.setDiaChi(nv.getN().getDiaChi());
        nguoiDung.setSoDienThoai(nv.getN().getSoDienThoai());
        nguoiDung.setEmail(nv.getN().getEmail());
        nguoiDung.setTrangThai(nv.getN().isTrangThai());
        nguoiDung.setVaiTro(vaiTroRepo.findById(nv.getN().getVaiTro().getId()).orElseThrow());

        if (matKhauMoi != null && !matKhauMoi.isEmpty()) {
            nguoiDung.setMatKhau_hash(passwordEncoder.encode(matKhauMoi));
        } else {
            nguoiDung.setMatKhau_hash(nv.getN().getMatKhau_hash());
        }

        nv.setN(nguoiDung);

        if (!nv.getN().getEmail().equals(oldEmail)
                && NguoiDungRepo.checkEmail(nv.getN().getEmail(), nv.getN().getMaNguoiDung())) {
            redirect.addFlashAttribute("error", "email đã tồn tại");
            return "redirect:/admin/nhan-vien";
        }

        NguoiDungRepo.save(nv.getN());
        repo.save(nv);
        redirect.addFlashAttribute("success", "cập nhật nhân viên thành công");
        return "redirect:/admin/nhan-vien";
    }


    @GetMapping("/edit/{id}")
    public String editNhanVien(Model model,Principal p,@PathVariable("id") int id){
        if(CheckRole(p.getName())){
            model.addAttribute("nhanViens",repo.findAll());
            model.addAttribute("nhanVien",repo.findbyid(id));
            model.addAttribute("nguoiDungs",repo.ListAdd());
            model.addAttribute("vaiTros",vaiTroRepo.findAll());

            return "admin/nhan-vien-list";
        }
        return "redirect:/home";
    }


    @GetMapping("/search")
    public String searchNhanVien(Model model, Principal p ,@RequestParam("keyword") String name , RedirectAttributes redirect){
        if (CheckRole(p.getName())){
            Nhanvien nv = new Nhanvien();
            nv.setN(new NguoiDung());
            model.addAttribute("nhanViens",repo.findByName(name));
            model.addAttribute("nhanVien",nv);
            model.addAttribute("nguoiDungs",repo.ListAdd());
            model.addAttribute("vaiTros",vaiTroRepo.findAll());
            redirect.addFlashAttribute("success","tổng số tìm được = " + repo.findByName(name).size());
            return "admin/nhan-vien-list";
        }
        return "redirect:/admin/nhan-vien";
    }
}
