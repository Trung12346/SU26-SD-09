package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.datPhong;
import su26sd09.su26sd09.repository.datPhongRepo;

import java.util.List;


@Service
public class datPhongService {

    @Autowired
    datPhongRepo repo;


    public List<datPhong> findAll(){
        return repo.findAll();
    }

    public Page<datPhong> findAll(Pageable page){
        return repo.findAll(page);
    }


    public void removePhong(datPhong d){
        repo.delete(d);
    }

    public void updatePhong(datPhong d){
        repo.save(d);
    }
    public void themPhong(datPhong d){
        repo.save(d);
    }

    public Page<datPhong> FindbyNguoiDung(int id,Pageable pageable){
        return repo.findByNguoiDung(id,pageable);
    }
    public List<datPhong> FindbyNguoiDung(int id){
        return repo.FindByNguoiDung(id);
    }
}
