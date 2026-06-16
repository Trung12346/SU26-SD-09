package su26sd09.su26sd09.repository;

<<<<<<< HEAD
=======
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
>>>>>>> master
import su26sd09.su26sd09.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

<<<<<<< HEAD
=======
import java.util.List;

>>>>>>> master
@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    public NguoiDung findByEmail(String email);
    NguoiDung findBySoDienThoai(String soDienThoai);
<<<<<<< HEAD
=======


    @Query("select n from NguoiDung n where n.hoTen like concat('%',:name,'%')")
    public List<NguoiDung> search(@Param("name") String name);
>>>>>>> master
}
