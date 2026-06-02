package su26sd09.su26sd09.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Table(name = "vai_tro")
public class vaiTro {
    @Id
    @Column(name = "ma_vai_tro")
    public int id;

    @Column(name = "ten_vai_tro")
    public String tenVaitro;
}
