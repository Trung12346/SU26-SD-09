package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.chiTietDatPhong;
import su26sd09.su26sd09.entity.datPhong;
import su26sd09.su26sd09.repository.chiTietDatPhongRepo;

import java.util.List;

@Service
public class chiTietDatPhongService {

    @Autowired
    chiTietDatPhongRepo repo;

    public List<chiTietDatPhong> findAll(){
        return repo.findAll();
    }

    public void remove(chiTietDatPhong c){
        repo.delete(c);
    }

    public chiTietDatPhong findbyId(int id){
        return repo.findById(id).orElse(null);
    }

    public void save(chiTietDatPhong c){
        repo.save(c);
    }


}
