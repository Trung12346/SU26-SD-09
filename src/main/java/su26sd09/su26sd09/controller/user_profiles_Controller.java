package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.danhGia;
import su26sd09.su26sd09.entity.datPhong;
import su26sd09.su26sd09.service.UserService;
import su26sd09.su26sd09.service.chiTietDatPhongService;
import su26sd09.su26sd09.service.danhGiaService;
import su26sd09.su26sd09.service.datPhongService;

import java.security.Principal;

@Controller
@RequestMapping("/profiles")
public class user_profiles_Controller {

    @Autowired
    UserService repo;
    @Autowired
    datPhongService datPhongRepo;
    @Autowired
    danhGiaService danhGiaRepo;
    @Autowired
    chiTietDatPhongService chitietPhongrepo;

    @GetMapping("")
    public String home(Model model,Principal p){

     NguoiDung nguoidung = new NguoiDung();

        for (NguoiDung n : repo.getAll()){
         if(n.getEmail().equals(p.getName())){
             nguoidung  = n;
         }
     }
        Pageable pageable = PageRequest.of(0,5);
        Page<datPhong> page = datPhongRepo.FindbyNguoiDung(nguoidung.getMaNguoiDung(), pageable);

        int phongdaDat = datPhongRepo.FindbyNguoiDung(nguoidung.getMaNguoiDung()).size() ;

        danhGia d = new danhGia();

        int tongsodanhgia = danhGiaRepo.findByNguoiDung(nguoidung.getMaNguoiDung()).size() ;




        model.addAttribute("listdatPhong",page);
        model.addAttribute("nguoiDung",nguoidung);
        model.addAttribute("tongPhong",phongdaDat);
        model.addAttribute("tongsodanhgia",tongsodanhgia);
      return "customer-setting";
    }
}
