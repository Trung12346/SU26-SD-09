package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.LoaiPhong;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.entity.DanhGia;
import su26sd09.su26sd09.service.DanhGiaService;
import su26sd09.su26sd09.service.PhongService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/phong")
public class PhongController {

    @Autowired
    private PhongService phongService;

    @Autowired
    private DanhGiaService danhGiaService;

    @GetMapping
    public String index(Model model) {
        // Lấy tất cả phòng
        List<Phong> phongs = phongService.findAllPhong();
        
        // Lấy tiện nghi cho từng phòng
        Map<Integer, List<String>> tienNghiTheoPhong = new HashMap<>();
        Map<Integer, String> tenLoaiTheoPhong = new HashMap<>();
        for (Phong phong : phongs) {
            tienNghiTheoPhong.put(phong.getMaPhong(), phongService.findTenTienNghiByPhong(phong.getMaPhong()));
            if (phong.getLoaiPhong() != null) {
                tenLoaiTheoPhong.put(phong.getMaPhong(), phong.getLoaiPhong().getTenLoai());
            }
        }
        
        // Lấy tất cả loại phòng cho dropdown menu
        List<LoaiPhong> loaiPhongs = phongService.findAllLoai();
        
        model.addAttribute("phongs", phongs);
        model.addAttribute("tienNghiTheoPhong", tienNghiTheoPhong);
        model.addAttribute("tenLoaiTheoPhong", tenLoaiTheoPhong);
        model.addAttribute("loaiPhongs", loaiPhongs);
        
        return "rooms";
    }

    @GetMapping("/{id}")
    public String detail(
            @PathVariable("id") int id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        Phong phong = phongService.findPhongById(id);
        if (phong == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng");
            return "redirect:/phong";
        }

        LoaiPhong loaiPhong = phong.getLoaiPhong();
        List<String> tienNghi = phongService.findTenTienNghiByPhong(phong.getMaPhong());
        List<DanhGia> danhGias = danhGiaService.findDaDuyetByPhong(phong.getMaPhong());
        double diemTrungBinh = danhGias.stream()
                .mapToInt(DanhGia::getDiemDanhGia)
                .average()
                .orElse(0);
        
        // Lấy tất cả loại phòng cho dropdown menu và carousel
        List<LoaiPhong> loaiPhongs = phongService.findAllLoai();
        Map<Integer, String> anhLoaiPhong = new HashMap<>();
        for (LoaiPhong lp : loaiPhongs) {
            anhLoaiPhong.put(lp.getId(), "https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=800&q=80");
        }

        model.addAttribute("phong", phong);
        model.addAttribute("loaiPhong", loaiPhong);
        model.addAttribute("tienNghi", tienNghi);
        model.addAttribute("danhGias", danhGias);
        model.addAttribute("tongDanhGia", danhGias.size());
        model.addAttribute("diemTrungBinh", diemTrungBinh);
        model.addAttribute("loaiPhongs", loaiPhongs);
        model.addAttribute("anhLoaiPhong", anhLoaiPhong);
        
        return "room-detail";
    }
}
