package su26sd09.su26sd09.entity;


import jakarta.persistence.*;
<<<<<<< HEAD
=======
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
>>>>>>> master
import lombok.Data;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "nguoi_dung")
<<<<<<< HEAD
public class NguoiDung {
=======
public class  NguoiDung {
>>>>>>> master
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nguoi_dung")
    private Integer maNguoiDung;



    @Column(name = "ho_ten", nullable = false, length = 150)
<<<<<<< HEAD
    private String hoTen;

    @Column(name = "email", nullable = false, length = 150, unique = true)
=======
    @NotBlank(message = "họ tên không được trống")
    private String hoTen;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    @NotBlank(message = "email không được trống")
>>>>>>> master
    private String email;

    @Column(name = "mat_khau_hash", nullable = false, length = 255)
    private String matKhau_hash;

    @Column(name = "so_dien_thoai", length = 20, unique = true)
<<<<<<< HEAD
    private String soDienThoai;

    @Column(name = "dia_chi", length = 300)
    private String diaChi;

    @Column(name = "trang_thai")
    private boolean trangThai = false;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao; // DB tự sinh GETDATE()

    @ManyToOne
    @JoinColumn(name = "ma_vai_tro",referencedColumnName = "ma_vai_tro")
=======
    @NotBlank(message = "số điện thoại không được trống")
    private String soDienThoai;

    @Column(name = "dia_chi", length = 300)
    @NotBlank(message = "địa chỉ không được trống")
    private String diaChi;

    @Column(name = "trang_thai")
    @NotNull(message = "trạng thái không được trống")
    private boolean trangThai = false;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_cap_nhat",insertable = false)
    private LocalDateTime ngayCapNhat;

    @ManyToOne
    @JoinColumn(name = "ma_vai_tro",referencedColumnName = "ma_vai_tro")
    @NotNull(message = "vai trò không được để trống")
>>>>>>> master
    private VaiTro vaiTro;
}
