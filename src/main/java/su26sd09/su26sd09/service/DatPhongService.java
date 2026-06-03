package su26sd09.su26sd09.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.repository.DatPhongRepo;

import java.util.List;


@Service
public class DatPhongService {

    @Autowired
    DatPhongRepo repo;


    public List<DatPhong> findAll(){
        return repo.findAll();
    }

    public Page<DatPhong> findAll(Pageable page){
        return repo.findAll(page);
    }


    public void removePhong(DatPhong d){
        repo.delete(d);
    }

    public void updatePhong(DatPhong d){
        repo.save(d);
    }
    public void themPhong(DatPhong d){
        repo.save(d);
    }

    public Page<DatPhong> FindbyNguoiDung(int id, Pageable pageable){
        return repo.findByNguoiDung(id,pageable);
    }
    public List<DatPhong> FindbyNguoiDung(int id){
        return repo.FindByNguoiDung(id);
    }
}
