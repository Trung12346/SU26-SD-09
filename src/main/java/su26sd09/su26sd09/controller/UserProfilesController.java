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
import su26sd09.su26sd09.entity.DanhGia;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.service.UserService;
import su26sd09.su26sd09.service.ChiTietDatPhongService;
import su26sd09.su26sd09.service.DanhGiaService;
import su26sd09.su26sd09.service.DatPhongService;

import java.security.Principal;

@Controller
@RequestMapping("/profiles")
public class UserProfilesController {

    @Autowired
    UserService repo;
    @Autowired
    DatPhongService datPhongRepo;
    @Autowired
    DanhGiaService danhGiaRepo;
    @Autowired
    ChiTietDatPhongService chitietPhongrepo;

    @GetMapping("")
    public String home(Model model,Principal p){

     NguoiDung nguoidung = new NguoiDung();

        for (NguoiDung n : repo.getAll()){
         if(n.getEmail().equals(p.getName())){
             nguoidung  = n;
         }
     }
        Pageable pageable = PageRequest.of(0,5);
        Page<DatPhong> page = datPhongRepo.FindbyNguoiDung(nguoidung.getMaNguoiDung(), pageable);

        int phongdaDat = datPhongRepo.FindbyNguoiDung(nguoidung.getMaNguoiDung()).size() ;

        DanhGia d = new DanhGia();

        int tongsodanhgia = danhGiaRepo.findByNguoiDung(nguoidung.getMaNguoiDung()).size() ;




        model.addAttribute("listdatPhong",page);
        model.addAttribute("nguoiDung",nguoidung);
        model.addAttribute("tongPhong",phongdaDat);
        model.addAttribute("tongsodanhgia",tongsodanhgia);
      return "customer-setting";
    }
}
