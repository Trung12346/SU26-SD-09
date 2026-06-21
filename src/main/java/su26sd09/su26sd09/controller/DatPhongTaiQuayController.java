package su26sd09.su26sd09.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.*;
import su26sd09.su26sd09.service.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/dat-phong-quay")
public class DatPhongTaiQuayController {
    @Autowired private PhongService phongService;
    @Autowired private DatPhongService datPhongService;
    @Autowired private ChiTietDatPhongService chiTietDatPhongService;
    @Autowired private DichVuService dichVuService;
    @Autowired private ChiTietDichVuService ctdvService;
    @Autowired private khuyenMaiService khuyenMaiService;
    @Autowired private HoaDonService hoaDonService;
    @Autowired private ThanhToanService thanhToanService;
    @Autowired private NguoiDungService nguoiDungService;
    @Autowired private NhanVienService nhanVienService;



    @GetMapping("")
    public String showForm(Model model, Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            NguoiDung n = nguoiDungService.findByEmail(authentication.getName());
            if (n == null) {
                return "redirect:/home";
            }

            Nhanvien nv = nhanVienService.findByMaNguoiDung(n.getMaNguoiDung());
            if (nv == null || !"Lễ Tân".equals(nv.getBoPhan())) {
                return "redirect:/home";
            }
        }

        model.addAttribute("phongTrongList", phongService.findByTrangThai("Trong"));
        model.addAttribute("dichVuList", dichVuService.findAll());
        model.addAttribute("khuyenMaiList", khuyenMaiService.findAllActive());
        return "admin/dat-phong-quay";
    }

    @PostMapping("/submit")
    public String submit(@RequestParam(required = false) String hoten,
                         @RequestParam(required = false) String email,
                         @RequestParam(required = false) String sdt,
                         @RequestParam("ma_cccd") String maCccd,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngaydatPhong,
                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngaytraPhong,
                         @RequestParam Integer songuoiLon,
                         @RequestParam Integer sotreEm,
                         @RequestParam(required = false) String yeuCauThem,
                         @RequestParam(required = false) Integer maKhuyenMai,
                         @RequestParam(value = "maPhongList", required = false) List<Integer> maPhongList,
                         @RequestParam(value = "dichVuIds", required = false) List<Integer> dichVuIds,
                         @RequestParam Map<String, String> allParams,
                         Authentication authentication,
                         RedirectAttributes redirectAttributes) {

        NguoiDung n = nguoiDungService.findByEmail(authentication.getName());

        Nhanvien nvCheck = nhanVienService.findByMaNguoiDung(n.getMaNguoiDung());

        if (nvCheck == null || !"Lễ Tân".equals(nvCheck.getBoPhan())) {
            System.out.println("khong khop bo phan");
            return "redirect:/home";
        }

        if (maCccd == null || maCccd.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "CCCD khong duoc de trong");
            return "redirect:/admin/dat-phong-quay";
        }
        if (maPhongList == null || maPhongList.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui long chon it nhat 1 phong");
            return "redirect:/admin/dat-phong-quay";
        }



        DatPhong dp = new DatPhong();
        dp.setHoten(hoten);
        dp.setEmail(email);
        dp.setSdt(sdt);
        dp.setMa_cccd(maCccd);
        dp.setNgaydatPhong(ngaydatPhong.atStartOfDay());
        dp.setNgaytraPhong(ngaytraPhong.atTime(12, 0));
        dp.setSonguoiLon(songuoiLon);
        dp.setSotreEm(sotreEm);
        dp.setYeuCauThem(yeuCauThem);
        dp.setTrangThai("Da nhan phong");
        dp.setNgayTao(LocalDateTime.now());


        if (maKhuyenMai != null) {
            KhuyenMai km = khuyenMaiService.findbyId(maKhuyenMai);
            dp.setKm(km);
        }

        NguoiDung staffDefault = nguoiDungService.findByEmail("staff@hotel.vn");

        if (authentication != null) {
            NguoiDung nd = nguoiDungService.findByEmail(authentication.getName());
            if (nd != null) {
                Nhanvien nv = nhanVienService.findByMaNguoiDung(nd.getMaNguoiDung());
                dp.setNv(nv != null ? nv : nhanVienService.findByMaNguoiDung(staffDefault.getMaNguoiDung()));
            } else {
                dp.setNv(nhanVienService.findByMaNguoiDung(staffDefault.getMaNguoiDung()));
            }
        } else {
            dp.setNv(nhanVienService.findByMaNguoiDung(staffDefault.getMaNguoiDung()));
        }

        DatPhong savedDp = datPhongService.save(dp);

        BigDecimal amountPhong = BigDecimal.ZERO;

        for (Integer maPhong : maPhongList) {
            Phong phong = phongService.findById(maPhong);
            if (phong == null || !"Trong".equals(phong.getTrangThai())) {
                continue;
            }

            ChiTietDatPhong ctdp = new ChiTietDatPhong();
            ctdp.setD(savedDp);
            ctdp.setP(phong);
            ctdp.setGiaMoiDem(phong.getGiaMoiDem());

            BigDecimal giaApDung = phong.getGiaMoiDem();

            if (phong.getKhuyenMai() != null) {
                KhuyenMai kmPhong = phong.getKhuyenMai();
                giaApDung = tinhGiaSauGiam(giaApDung, kmPhong);
            }

            if (maKhuyenMai != null) {
                KhuyenMai kmDon = khuyenMaiService.findbyId(maKhuyenMai);
                if (kmDon != null) {
                    giaApDung = tinhGiaSauGiam(giaApDung, kmDon);
                }
            }

            ctdp.setGiaKhiDat(giaApDung);
            chiTietDatPhongService.save(ctdp);

            amountPhong = amountPhong.add(giaApDung);

            phong.setTrangThai("Dang su dung");
            phongService.save1(phong);
        }

        BigDecimal amountDv = BigDecimal.ZERO;

        if (dichVuIds != null) {
            for (Integer maDichVu : dichVuIds) {
                Dich_vu dv = dichVuService.findById(maDichVu);
                if (dv == null) continue;

                String slStr = allParams.get("soLuong_" + maDichVu);
                int sl = (slStr != null && !slStr.isBlank()) ? Integer.parseInt(slStr) : 1;

                BigDecimal thanhTien = dv.getGia().multiply(BigDecimal.valueOf(sl));

                Chi_tiet_dich_vu ct = new Chi_tiet_dich_vu();
                ct.setDatPhong(savedDp);
                ct.setDv(dv);
                ct.setSoluong(sl);
                ct.setDonGia(thanhTien);
                ct.setNgay_su_dung(LocalDateTime.now());
                ctdvService.save(ct);

                amountDv = amountDv.add(thanhTien);
            }
        }

        BigDecimal VATCD = new BigDecimal("0.10");
        BigDecimal tongTienTruocVat = amountPhong.add(amountDv);
        BigDecimal tienVat = tongTienTruocVat.multiply(VATCD).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tongCong = tongTienTruocVat.add(tienVat);

        HoaDon hd = new HoaDon();
        hd.setNgayXuat(LocalDateTime.now());
        hd.setD(savedDp);
        hd.setTienPhong(amountPhong);
        hd.setTienDichVu(amountDv);
        hd.setTienGiam(BigDecimal.ZERO);
        hd.setTienVat(tienVat);
        hd.setTongTien(tongCong);
        hd.setDaThanhToan(tongCong);
        hd.setGhiChu("Dat phong va thanh toan tien mat tai quay ma don: " + savedDp.getId());
        hoaDonService.save(hd);

        ThanhToan tt = new ThanhToan();
        tt.setH(hd);
        tt.setPhuongThuc("Tien Mat");
        tt.setSoTien(tongCong);
        tt.setTrangThai("Thanh cong");
        tt.setNgaythanhToan(LocalDateTime.now());
        tt.setGichu("Thu tien mat tai quay da nhan du 100%");

        if (authentication != null) {
            NguoiDung nd = nguoiDungService.findByEmail(authentication.getName());
            if (nd != null) {
                Nhanvien nv = nhanVienService.findByMaNguoiDung(nd.getMaNguoiDung());
                tt.setNv(nv != null ? nv : nhanVienService.findByMaNguoiDung(nguoiDungService.findByEmail("staff@hotel.vn").getMaNguoiDung()));
            } else {
                tt.setNv(nhanVienService.findbyid((nguoiDungService.findByEmail("staff@hotel.vn").getMaNguoiDung())));
            }
        } else {
            tt.setNv(nhanVienService.findbyid((nguoiDungService.findByEmail("staff@hotel.vn").getMaNguoiDung())));
        }
        System.out.println(nguoiDungService.findByEmail("staff@hotel.vn").getMaNguoiDung());
        thanhToanService.save(tt);
        redirectAttributes.addFlashAttribute("success",
                "Tao don thanh cong, ma don: " + savedDp.getId() + ", tong tien da thu: " + tongCong + " VND");
        return "redirect:/admin/dat-phong";
    }

    private BigDecimal tinhGiaSauGiam(BigDecimal giaGoc, KhuyenMai km) {
        if ("PERCENT".equals(km.getLoaiGiam())) {
            BigDecimal phanTramGiam = km.getGiatriGiam().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal heSoConLai = BigDecimal.ONE.subtract(phanTramGiam);
            return giaGoc.multiply(heSoConLai);
        } else if ("FIXED".equals(km.getLoaiGiam())) {
            BigDecimal giaSauGiam = giaGoc.subtract(km.getGiatriGiam());
            return giaSauGiam.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : giaSauGiam;
        }
        return giaGoc;
    }
}

