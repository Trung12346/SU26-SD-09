package su26sd09.su26sd09.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "nguoi_dung")
public class NguoiDung {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;



    @Column(name = "ho_ten", nullable = false, length = 150)
    private String hoTen;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Column(name = "mat_khau_hash", nullable = false, length = 255)
    private String matKhau_hash;

    @Column(name = "so_dien_thoai", length = 20, unique = true)
    private String soDienThoai;

    @Column(name = "dia_chi", length = 300)
    private String diaChi;

    @Column(name = "trang_thai")
    private boolean trangThai = false;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao; // DB tự sinh GETDATE()

    @ManyToOne
    @JoinColumn(name = "ma_vai_tro",referencedColumnName = "ma_vai_tro")
    private VaiTro vaiTro;
}
