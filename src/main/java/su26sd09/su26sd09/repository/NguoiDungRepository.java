package su26sd09.su26sd09.repository;

import su26sd09.su26sd09.entity.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Integer> {
    boolean existsByEmail(String email);
    boolean existsBySoDienThoai(String soDienThoai);
    boolean existsByMaCccd(String maCccd);
    public NguoiDung findByEmail(String email);
}