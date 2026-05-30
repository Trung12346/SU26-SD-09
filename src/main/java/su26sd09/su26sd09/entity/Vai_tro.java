package su26sd09.su26sd09.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "vai_tro")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vai_tro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_vai_tro")
    private Integer id;

    @Column(name = "ten_vai_tro")
    private String ten_vai_tro;

    @OneToMany(mappedBy = "vai_tro",cascade = CascadeType.ALL)
    private List<NguoiDung> nguoiDung;
}
