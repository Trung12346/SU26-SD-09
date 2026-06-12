package su26sd09.su26sd09.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import su26sd09.su26sd09.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    public NguoiDung findByEmail(String email);
    NguoiDung findBySoDienThoai(String soDienThoai);


    @Query("select n from NguoiDung n where n.hoTen like concat('%',:name,'%')")
    public List<NguoiDung> search(@Param("name") String name);
}
