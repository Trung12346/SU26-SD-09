package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chi_tiet_dat_phong")
public class ChiTietDatPhong {
    @Id
    @Column(name = "ma_chi_tiet")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;

    @ManyToOne
    @JoinColumn(name = "ma_dat_phong")
    public DatPhong d;

    @ManyToOne
    @JoinColumn(name = "ma_phong")
    public Phong p;


}
