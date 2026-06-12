package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "nhan_vien")
public class Nhanvien {
    @Id
    @Column(name = "ma_nhan_vien")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "ma_nguoi_dung")
    @NotNull(message = "vui lòng chọn tài khoản tương ứng")
    @Valid
    public NguoiDung n;

    @Column(name = "bo_phan")
    @NotBlank(message = "bộ phận không được để trống")
    public String boPhan;


}
