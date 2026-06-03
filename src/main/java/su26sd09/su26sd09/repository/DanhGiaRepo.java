package su26sd09.su26sd09.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su26sd09.su26sd09.entity.DanhGia;

import java.util.List;

public interface DanhGiaRepo extends JpaRepository<DanhGia,Integer> {
     @Query("select d from DanhGia d where d.n.maNguoiDung = :id")
    Page<DanhGia> findByNguoiDung(int id , Pageable pageable);

     @Query("select d from DanhGia d where d.n.maNguoiDung = :id")
     List<DanhGia> FindByNguoiDung(int id);
}
