package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.danhGia;
import su26sd09.su26sd09.repository.danhGiaRepo;

import java.util.List;

@Service
public class danhGiaService {


    @Autowired
    danhGiaRepo repo;


    public List<danhGia> findAll(){
        return repo.findAll();
    }

    public danhGia findByid(int id){
        return repo.findById(id).orElse(null);
    }

    public void remove(danhGia danhgia){
        repo.delete(danhgia);
    }
    public void save(danhGia danhGia){
        repo.save(danhGia);
    }
    public Page<danhGia> findByNguoiDung(int id , Pageable pageable){
      return repo.findByNguoiDung(id,pageable);
    }

    public List<danhGia> findByNguoiDung(int id ){
      return repo.FindByNguoiDung(id);
    }
}
