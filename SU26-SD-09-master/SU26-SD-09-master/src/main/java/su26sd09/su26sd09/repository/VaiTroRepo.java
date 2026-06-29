package su26sd09.su26sd09.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su26sd09.su26sd09.entity.VaiTro;

import java.util.Optional;

public interface VaiTroRepo extends JpaRepository<VaiTro,Integer> {

    @Query(value = "select vt from VaiTro vt where vt.tenVaiTro = :name")
    public VaiTro findbyNameVaiTro(@Param("name") String name);
}
