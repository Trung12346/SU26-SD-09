package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import su26sd09.su26sd09.entity.LoaiPhong;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.service.CustomerUserDetailsService;
import su26sd09.su26sd09.service.PhongService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequestMapping("/home")
@Controller
public class Home {

    @Autowired
    CustomerUserDetailsService repo;

    @Autowired
    NguoiDungRepository UserRepo;

    @Autowired
    private PhongService phongService;

    @GetMapping("")
    public String home(@RequestParam(name = "tenPhong", required = false) String tenPhong,
                       @RequestParam(name = "tenLoaiPhong", required = false) String tenLoaiPhong,
                       @RequestParam(name = "minGia", required = false) BigDecimal minGia,
                       @RequestParam(name = "maxGia", required = false) BigDecimal maxGia,
                       Model model) {
        List<LoaiPhong> loaiPhongs = phongService.findAllLoai();
        Map<Integer, Long> soPhongTrongTheoLoai = new HashMap<>();
        Map<Integer, String> anhLoaiPhong = new HashMap<>();

        for (LoaiPhong loai : loaiPhongs) {
            soPhongTrongTheoLoai.put(loai.getId(), phongService.countPhongTrongTheoLoai(loai.getId()));
            anhLoaiPhong.put(loai.getId(), "https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80");
        }

        boolean dangTimKiem = hasText(tenPhong) || hasText(tenLoaiPhong) || minGia != null || maxGia != null;
        boolean khoangGiaKhongHopLe = minGia != null && maxGia != null && minGia.compareTo(maxGia) > 0;
        model.addAttribute("dangTimKiem", dangTimKiem);
        if (dangTimKiem) {
            if (khoangGiaKhongHopLe) {
                model.addAttribute("homeSearchError", "Gia tu phai thap hon hoac bang Gia den.");
                model.addAttribute("phongsTimKiem", List.of());
            } else {
                List<Phong> phongsTimKiem = phongService.searchPhongTrongPublic(tenPhong, tenLoaiPhong, minGia, maxGia);
                model.addAttribute("phongsTimKiem", phongsTimKiem);
            }
        }

        model.addAttribute("loaiPhongs", loaiPhongs);
        model.addAttribute("soPhongTrongTheoLoai", soPhongTrongTheoLoai);
        model.addAttribute("anhLoaiPhong", anhLoaiPhong);
        model.addAttribute("tenPhong", tenPhong);
        model.addAttribute("tenLoaiPhong", tenLoaiPhong);
        model.addAttribute("minGia", minGia);
        model.addAttribute("maxGia", maxGia);

        return "index";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}