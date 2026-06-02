package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "loai_phong")
public class loaiPhong {
    @Id
    @Column(name = "ma_loai_phong")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @Column(name = "ten_loai_phong")
    public String tenLoaiPhong;

    @Column(name = "suc_chua_toi_da")
    public int SucChuaToiDa;

    @Column(name = "gia_co_ban")
    public BigDecimal giaCoBan;

    @Column(name = "mo_ta")
    public String mota;

}