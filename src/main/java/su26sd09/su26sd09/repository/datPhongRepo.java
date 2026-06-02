package su26sd09.su26sd09.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.datPhong;

import java.util.List;

@Repository
public interface datPhongRepo extends JpaRepository<datPhong,Integer> {

    @Query("select d from datPhong d where d.n.maNguoiDung = :id")
    Page<datPhong> findByNguoiDung(int id,Pageable pageable);

    @Query("select d from datPhong d where d.n.maNguoiDung = :id")
    List<datPhong> FindByNguoiDung(int id);
 }
