package su26sd09.su26sd09.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.danhGia;

import java.util.List;

public interface danhGiaRepo extends JpaRepository<danhGia,Integer> {
     @Query("select d from danhGia d where d.n.maNguoiDung = :id")
    Page<danhGia> findByNguoiDung(int id , Pageable pageable);

     @Query("select d from danhGia d where d.n.maNguoiDung = :id")
     List<danhGia> FindByNguoiDung(int id);
}
