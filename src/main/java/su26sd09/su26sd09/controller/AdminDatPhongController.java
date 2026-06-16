package su26sd09.su26sd09.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.HoaDon;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.service.DatPhongService;
import su26sd09.su26sd09.service.HoaDonService;
import su26sd09.su26sd09.service.PhongService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/dat-phong")
public class AdminDatPhongController {

    @Autowired
    DatPhongService datPhongService;


    @Autowired
    HoaDonService hoaDonService;

    @Autowired
    PhongService phongService;

    @GetMapping("")
    public String GetDatPhong(Model model){
        List<DatPhong> datPhongs = datPhongService.findAll();

        List<Integer> daDatHoaDon = hoaDonService.findAll()
                .stream()
                .filter(hd -> hd.getD() != null)
                .map(hd -> hd.getD().getId())
                .collect(Collectors.toList());
        model.addAttribute("daDatHoaDon", daDatHoaDon);

        Map<Integer,List<Phong>>PhongTheoDon = new HashMap<>();

        for(DatPhong dp: datPhongs){
            PhongTheoDon.put(dp.getId(),datPhongService.findPhongByDatPhongId(dp.getId()));
        }
        model.addAttribute("datPhongs",datPhongService.findAll());
        model.addAttribute("phongTheoDon",PhongTheoDon);
        return "admin/dat-phong-list";
    }

    @GetMapping("/search")
    public String getSearchDatPhong(
            @RequestParam(required = false) Integer maDatPhong,
            @RequestParam(required = false) String tenKhach,
            @RequestParam(required = false) Integer maNhanVien,
            @RequestParam(required = false) String ma_cccd,
            @RequestParam(required = false) String ngayNhanTu,
            @RequestParam(required = false) String ngayNhanDen,
            @RequestParam(required = false) String ngayTraTu,
            @RequestParam(required = false) String ngayTraDen,
            @RequestParam(required = false) Integer soNguoiLon,
            @RequestParam(required = false) Integer soTreEm,
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String yeuCauThem,
            @RequestParam(required = false) String ngayTaoTu,
            @RequestParam(required = false) String ngayTaoDen,
            @RequestParam(required = false) String ngayCapNhatTu,
            @RequestParam(required = false) String ngayCapNhatDen,
            Model model) {

        List<Integer> daDatHoaDon = hoaDonService.findAll()
                .stream()
                .filter(hd -> hd.getD() != null)  
                .map(hd -> hd.getD().getId())
                .collect(Collectors.toList());

        model.addAttribute("daDatHoaDon", daDatHoaDon);


        List<DatPhong> datPhongs = datPhongService.search(
                maDatPhong, tenKhach, maNhanVien, ma_cccd,
                ngayNhanTu, ngayNhanDen, ngayTraTu, ngayTraDen,
                soNguoiLon, soTreEm, trangThai, yeuCauThem,
                ngayTaoTu, ngayTaoDen, ngayCapNhatTu, ngayCapNhatDen
        );

        if(tenKhach!=null){
            System.out.println("Found!");
        }else{
            System.out.println("NOt Found: "+tenKhach);
        }

        Map<Integer, List<Phong>> phongTheoDon = new HashMap<>();
        for (DatPhong dp : datPhongs) {
            phongTheoDon.put(dp.getId(), datPhongService.findPhongByDatPhongId(dp.getId()));
        }

        model.addAttribute("datPhongs", datPhongs);
        model.addAttribute("phongTheoDon", phongTheoDon);

        model.addAttribute("maDatPhong", maDatPhong);
        model.addAttribute("tenKhach", tenKhach);
        model.addAttribute("maNhanVien", maNhanVien);
        model.addAttribute("ma_cccd", ma_cccd);
        model.addAttribute("ngayNhanTu", ngayNhanTu);
        model.addAttribute("ngayNhanDen", ngayNhanDen);
        model.addAttribute("ngayTraTu", ngayTraTu);
        model.addAttribute("ngayTraDen", ngayTraDen);
        model.addAttribute("soNguoiLon", soNguoiLon);
        model.addAttribute("soTreEm", soTreEm);
        model.addAttribute("trangThai", trangThai);
        model.addAttribute("yeuCauThem", yeuCauThem);
        model.addAttribute("ngayTaoTu", ngayTaoTu);
        model.addAttribute("ngayTaoDen", ngayTaoDen);
        model.addAttribute("ngayCapNhatTu", ngayCapNhatTu);
        model.addAttribute("ngayCapNhatDen", ngayCapNhatDen);

        return "admin/dat-phong-list";
    }

    @PostMapping("/update-trang-thai")
    public String updateTrangThai(@RequestParam Integer id,
                                  @RequestParam String trangThai,
                                  RedirectAttributes redirectAttributes) {
        DatPhong dp = datPhongService.findById(id);
        if (dp == null) {
            redirectAttributes.addFlashAttribute("error", "khong tim thay don dat phong");
            return "redirect:/admin/dat-phong";
        }
        dp.setTrangThai(trangThai);
        datPhongService.save(dp);
        updateRoomStatusForBooking(dp, trangThai);

        redirectAttributes.addFlashAttribute("success", "Cap nhat trang thai thanh cong");
        return "redirect:/admin/dat-phong";
    }

    private void updateRoomStatusForBooking(DatPhong datPhong, String trangThai) {
        String roomStatus = null;
        if ("Da nhan phong".equals(trangThai)) {
            roomStatus = "Dang su dung";
        } else if ("Da tra phong".equals(trangThai) || "Da huy".equals(trangThai)) {
            roomStatus = "Trong";
        } else if ("Da xac nhan".equals(trangThai)) {
            roomStatus = "Da dat";
        }

        if (roomStatus == null) {
            return;
        }
        for (Phong phong : datPhongService.findPhongByDatPhongId(datPhong.getId())) {
            phongService.updateTrangThai(phong.getMaPhong(), roomStatus);
        }
    }

}
