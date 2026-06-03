package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "khuyen_mai")
public class KhuyenMai {
    @Id
    @Column(name = "ma_khuyen_mai")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_tao")
    public NguoiDung n;

    @Column(name = "promo_code")
    public String promoCode;

    @Column(name = "mo_ta")
    public String moTa;

    @Column(name = "loai_giam")
    public String loaiGiam;

    @Column(name = "gia_tri_giam",precision = 12,scale = 2)
    public BigDecimal giatriGiam;

    @Column(name = "ngay_bat_dau")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate ngayKetThuc;

    @Column(name = "hoat_dong")
    public boolean hoatDong;

    @Column(name = "ngay_tao")
    public LocalDateTime ngayTao;


}
