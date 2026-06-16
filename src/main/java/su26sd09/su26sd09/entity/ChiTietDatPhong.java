package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

<<<<<<< HEAD
=======
import java.math.BigDecimal;

>>>>>>> master
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chi_tiet_dat_phong")
public class ChiTietDatPhong {
    @Id
<<<<<<< HEAD
    @Column(name = "ma_chi_tiet")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "ma_dat_phong")
    public DatPhong d;

    @ManyToOne
    @JoinColumn(name = "ma_phong")
    public Phong p;
=======
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_chi_tiet")
    private int id;

    @ManyToOne
    @JoinColumn(name = "ma_dat_phong")
    private DatPhong d;

    @ManyToOne
    @JoinColumn(name = "ma_phong")
    private Phong p;

    @Column(name ="gia_moi_dem")
    private BigDecimal giaMoiDem;

    @Column(name = "gia_khi_dat")
    private BigDecimal giaKhiDat;
>>>>>>> master


}
