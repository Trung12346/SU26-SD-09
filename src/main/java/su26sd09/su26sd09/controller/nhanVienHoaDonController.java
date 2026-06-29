package su26sd09.su26sd09.controller;

import com.lowagie.text.pdf.BaseFont;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;
import su26sd09.su26sd09.entity.*;
import su26sd09.su26sd09.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/nhan-vien/hoa-don")
public class nhanVienHoaDonController {

    @Autowired private HoaDonService hoaDonService;
    @Autowired private ChiTietDichVuService chiTietDichVuService;
    @Autowired private NguoiDungService nguoiDungService;
    @Autowired private NhanVienService nhanVienService;
    @Autowired private TemplateEngine templateEngine;

    /**
     * Kiểm tra nhân viên có phải Lễ Tân không.
     * Trả về true nếu hợp lệ, false nếu không.
     */
    private boolean isLeTan(Authentication authentication) {
        if (authentication == null) return false;
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return true;

        NguoiDung nd = nguoiDungService.findByEmail(authentication.getName());
        if (nd == null) return false;
        Nhanvien nv = nhanVienService.findByMaNguoiDung(nd.getMaNguoiDung());
        return nv != null && "Lễ Tân".equalsIgnoreCase(nv.getBoPhan());
    }

    @GetMapping("")
    public String getHoaDon(
            @RequestParam(required = false) Integer maHoaDon,
            @RequestParam(required = false) Integer maDatPhong,
            @RequestParam(required = false) String tenKhach,
            @RequestParam(required = false) String maKhuyenMai,
            @RequestParam(required = false) String ngayXuatTu,
            @RequestParam(required = false) String ngayXuatDen,
            @RequestParam(required = false) BigDecimal tongTienTu,
            @RequestParam(required = false) BigDecimal tongTienDen,
            @RequestParam(required = false) String trangThaiThanhToan,
            Authentication authentication,
            Model model) {

        if (!isLeTan(authentication)) return "redirect:/home";

        List<HoaDon> hoaDons = hoaDonService.findAll().stream()
                .filter(hd -> {
                    if (maHoaDon != null && hd.getId() != maHoaDon) return false;

                    if (maDatPhong != null &&
                            (hd.getD() == null || hd.getD().getId() != maDatPhong))
                        return false;

                    if (tenKhach != null && !tenKhach.isEmpty()) {
                        String ten = "";
                        if (hd.getD() != null) {
                            if (hd.getD().getHoten() != null) ten = hd.getD().getHoten();
                            else if (hd.getD().getN() != null) ten = hd.getD().getN().getHoTen();
                        }
                        if (!ten.toLowerCase().contains(tenKhach.toLowerCase())) return false;
                    }

                    if (maKhuyenMai != null && !maKhuyenMai.isEmpty() &&
                            (hd.getK() == null ||
                                    !hd.getK().getPromoCode()
                                            .toLowerCase()
                                            .contains(maKhuyenMai.toLowerCase())))
                        return false;

                    if (tongTienTu != null && hd.getTongTien() != null &&
                            hd.getTongTien().compareTo(tongTienTu) < 0) return false;

                    if (tongTienDen != null && hd.getTongTien() != null &&
                            hd.getTongTien().compareTo(tongTienDen) > 0) return false;

                    if (ngayXuatTu != null && !ngayXuatTu.isEmpty()) {
                        LocalDateTime tu = LocalDate.parse(ngayXuatTu).atStartOfDay();
                        if (hd.getNgayXuat() == null || hd.getNgayXuat().isBefore(tu))
                            return false;
                    }

                    if (ngayXuatDen != null && !ngayXuatDen.isEmpty()) {
                        LocalDateTime den = LocalDate.parse(ngayXuatDen).atTime(23, 59, 59);
                        if (hd.getNgayXuat() == null || hd.getNgayXuat().isAfter(den))
                            return false;
                    }

                    if (trangThaiThanhToan != null && !trangThaiThanhToan.isEmpty()
                            && hd.getTongTien() != null && hd.getDaThanhToan() != null) {
                        BigDecimal conNo = hd.getTongTien().subtract(hd.getDaThanhToan());
                        if ("chua".equals(trangThaiThanhToan) &&
                                hd.getDaThanhToan().compareTo(BigDecimal.ZERO) != 0)
                            return false;
                        if ("mot_phan".equals(trangThaiThanhToan) &&
                                conNo.compareTo(BigDecimal.ZERO) <= 0)
                            return false;
                        if ("du".equals(trangThaiThanhToan) &&
                                conNo.compareTo(BigDecimal.ZERO) != 0)
                            return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        Map<Integer, List<Chi_tiet_dich_vu>> dvMap = new HashMap<>();
        for (HoaDon hd : hoaDons) {
            if (hd.getD() != null) {
                dvMap.put(hd.getId(),
                        chiTietDichVuService.findByDatPhongId(hd.getD().getId()));
            }
        }

        model.addAttribute("hoaDons", hoaDons);
        model.addAttribute("dvMap", dvMap);

        // Giữ lại giá trị filter để hiển thị lại trên form
        model.addAttribute("maHoaDon", maHoaDon);
        model.addAttribute("maDatPhong", maDatPhong);
        model.addAttribute("tenKhach", tenKhach);
        model.addAttribute("maKhuyenMai", maKhuyenMai);
        model.addAttribute("ngayXuatTu", ngayXuatTu);
        model.addAttribute("ngayXuatDen", ngayXuatDen);
        model.addAttribute("tongTienTu", tongTienTu);
        model.addAttribute("tongTienDen", tongTienDen);
        model.addAttribute("trangThaiThanhToan", trangThaiThanhToan);

        return "nhan-vien/hoa-don-list";
    }

    @GetMapping("/xuat-pdf/{id}")
    public void xuatPdf(@PathVariable int id,
                        Authentication authentication,
                        HttpServletResponse response) throws Exception {

        if (!isLeTan(authentication)) {
            response.sendRedirect("/home");
            return;
        }

        HoaDon hoaDon = hoaDonService.findById(id);
        if (hoaDon == null) {
            response.sendRedirect("/nhan-vien/hoa-don");
            return;
        }

        Context context = new Context();
        context.setVariable("hoaDon", hoaDon);

        // Dùng chung template PDF với admin
        String html = templateEngine.process("nhan-vien/hoa-don-pdf", context);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition",
                "attachment; filename=hoa-don-" + id + ".pdf");

        ITextRenderer renderer = new ITextRenderer();
        renderer.getFontResolver().addFont(
                "C:/Windows/Fonts/arial.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED
        );
        renderer.setDocumentFromString(html);
        renderer.layout();
        renderer.createPDF(response.getOutputStream());
    }
}