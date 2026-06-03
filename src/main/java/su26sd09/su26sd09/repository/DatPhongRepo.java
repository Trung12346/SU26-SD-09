package su26sd09.su26sd09.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import su26sd09.su26sd09.entity.DatPhong;

import java.util.List;

@Repository
public interface DatPhongRepo extends JpaRepository<DatPhong,Integer> {

    @Query("select d from DatPhong d where d.n.maNguoiDung = :id")
    Page<DatPhong> findByNguoiDung(int id, Pageable pageable);

    @Query("select d from DatPhong d where d.n.maNguoiDung = :id")
    List<DatPhong> FindByNguoiDung(int id);
 }
