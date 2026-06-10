package su26sd09.su26sd09.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import su26sd09.su26sd09.entity.Nhanvien;
import su26sd09.su26sd09.repository.NhanVienRepo;

import java.util.List;

@Service
public class NhanVienService {
    @Autowired
    NhanVienRepo repo;

    public List<Nhanvien> findAll(){
        return repo.findAll();
    }

    public Nhanvien findbyid(int id){
        return repo.findById(id).orElse(null);

    }

    public void delete(Nhanvien n){
        repo.delete(n);
    }

    public void save(Nhanvien n){
        repo.save(n);
    }

    public List<Nhanvien> findByName(String name){
        return repo.findbyName(name);
    }

}
