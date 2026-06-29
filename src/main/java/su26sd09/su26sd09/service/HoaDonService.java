package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.ChiTietDatPhong;
import su26sd09.su26sd09.entity.Chi_tiet_dich_vu;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.HoaDon;
import su26sd09.su26sd09.repository.HoaDonRepo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class HoaDonService {

    @Autowired
    HoaDonRepo hoaDonRepo;

    @Autowired
    private ChiTietDatPhongService chiTietDatPhongService;

    @Autowired
    private ChiTietDichVuService chiTietDichVuService;

    @Autowired
    private DatPhongService datPhongService;

    public static BigDecimal VAT = new BigDecimal("0.10");

    public List<HoaDon> findAll(){
        return hoaDonRepo.findAll();
    }
    public HoaDon save(HoaDon hd){
        return hoaDonRepo.save(hd);
    }
    public HoaDon findById(Integer id){
        return hoaDonRepo.findById(id).orElse(null);
    }

    public HoaDon findByDatPhongId(Integer maDatPhong) {
        return hoaDonRepo.findByD_Id(maDatPhong);
    }

    public HoaDon tinhLaiHoaDon(Integer maDatPhong) {
        DatPhong dp = datPhongService.findById(maDatPhong);
        if (dp == null) {
            return null;
        }

        List<ChiTietDatPhong> ctdpList = chiTietDatPhongService.findByDatPhongId(maDatPhong);
        List<Chi_tiet_dich_vu> ctdvList = chiTietDichVuService.findByDatPhongId(maDatPhong);

        BigDecimal tienPhong = BigDecimal.ZERO;
        for (ChiTietDatPhong ctdp : ctdpList) {
            tienPhong = tienPhong.add(ctdp.getGiaKhiDat());
        }

        BigDecimal tienDichVu = BigDecimal.ZERO;
        for (Chi_tiet_dich_vu ctdv : ctdvList) {
            tienDichVu = tienDichVu.add(ctdv.getDonGia());
        }

        BigDecimal tongTruocVat = tienPhong.add(tienDichVu);
        BigDecimal tienVat = tongTruocVat.multiply(VAT).setScale(2, RoundingMode.HALF_UP);
        BigDecimal tongTien = tongTruocVat.add(tienVat);

        HoaDon hd = findByDatPhongId(maDatPhong);
        if (hd == null) {
            hd = new HoaDon();
            hd.setD(dp);
            hd.setTienGiam(BigDecimal.ZERO);
            hd.setDaThanhToan(BigDecimal.ZERO);
        }

        hd.setTienPhong(tienPhong);
        hd.setTienDichVu(tienDichVu);
        hd.setTienVat(tienVat);
        hd.setTongTien(tongTien);

        return hd;
    }
    public HoaDon taoHoacCapNhatHoaDon(Integer maDatPhong) {
        HoaDon hd = tinhLaiHoaDon(maDatPhong);
        if (hd == null) {
            return null;
        }
        if (hd.getId() == 0) {
            hd.setNgayXuat(LocalDateTime.now());
        } else {
            hd.setNgayCapNhat(LocalDateTime.now());
        }
        return hoaDonRepo.save(hd);
    }
}
