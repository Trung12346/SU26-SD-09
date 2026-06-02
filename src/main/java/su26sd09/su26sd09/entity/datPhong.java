package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "dat_phong")
public class datPhong {


    @Id
    @Column(name = "ma_dat_phong")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "ma_khach")
    public NguoiDung n;

    @JoinColumn(name = "ma_nhan_vien")
    @ManyToOne
    public NguoiDung nv;

    @Column(name = "ngay_nhan_phong")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:MM:ss")
    public LocalDateTime ngaydatPhong;

    @Column(name = "ngay_tra_phong")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:MM:ss")
    public LocalDateTime ngaytraPhong;

    @Column(name = "so_nguoi_lon")
    public int songuoiLon;

    @Column(name = "so_tre_em")
    public int sotreEm;

    @Column(name = "yeu_cau_them")
    public String yeuCauThem;

    @Column(name = "trang_thai")
    public Boolean trangThai;

    @Column(name = "ngay_tao")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:MM:ss")
    public LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat")
    @DateTimeFormat(pattern = "yyyy-MM-dd hh:MM:ss")
    public LocalDateTime ngayCapNhat;

    @Column(name = "Ma_cccd")
    public String macccd;





}
