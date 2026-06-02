package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.repository.NguoiDungRepository;

import java.util.List;

@Service
public class UserService {

    @Autowired
    NguoiDungRepository repo;

    public List<NguoiDung> getAll(){
        return repo.findAll();
    }

    public NguoiDung Getbyid(int id){
        return repo.findById(id).orElse(null);
    }

    public void remove(NguoiDung nguoiDung){
        repo.delete(nguoiDung);
    }

    public void save(NguoiDung nguoiDung){
        repo.save(nguoiDung);
    }


}
