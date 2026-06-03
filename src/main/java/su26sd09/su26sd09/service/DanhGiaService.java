package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.DanhGia;
import su26sd09.su26sd09.repository.DanhGiaRepo;

import java.util.List;

@Service
public class DanhGiaService {


    @Autowired
    DanhGiaRepo repo;


    public List<DanhGia> findAll(){
        return repo.findAll();
    }

    public DanhGia findByid(int id){
        return repo.findById(id).orElse(null);
    }

    public void remove(DanhGia danhgia){
        repo.delete(danhgia);
    }
    public void save(DanhGia danhGia){
        repo.save(danhGia);
    }
    public Page<DanhGia> findByNguoiDung(int id , Pageable pageable){
      return repo.findByNguoiDung(id,pageable);
    }

    public List<DanhGia> findByNguoiDung(int id ){
      return repo.FindByNguoiDung(id);
    }
}
