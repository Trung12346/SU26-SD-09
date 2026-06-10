package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
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
    public NguoiDung n;

    @Column(name = "bo_phan")
    public String boPhan;

    @Column(name = "ca_lam")
    public String caLam;

//    @Column(name = "bat_dau_lam")
//    public LocalDateTime batDauLam;
//
//    @Column(name = "ket_thuc_lam")
//    public LocalDateTime ketThucLam;
}
