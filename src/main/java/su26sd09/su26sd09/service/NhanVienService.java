package su26sd09.su26sd09.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Nhanvien;
import su26sd09.su26sd09.repository.NhanVienRepo;

import java.util.ArrayList;
import java.util.List;

@Service
public class NhanVienService {
    @Autowired
    NhanVienRepo repo;
    @Autowired
    UserService NguoiDungRepo;


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
        if (n.n.getVaiTro().getTenVaiTro().equals("ROLE_ADMIN") || n.n.getVaiTro().getTenVaiTro().equals("ROLE_EMPLOYEE")){
            NguoiDungRepo.save(n.n);
            return ;
        }
        if (n.n.isTrangThai() != true){
            NguoiDungRepo.save(n.n);
            return;
        }

        repo.save(n);
    }

    public List<Nhanvien> findByName(String name){
        return repo.findbyName(name);
    }

    public List<Nhanvien> ListAdd(){
        List<Nhanvien> Listnv = new ArrayList<>();
        for (NguoiDung n : NguoiDungRepo.getAll()){
            if(n.getVaiTro().getTenVaiTro().equals("ROLE_STAFF") && n.isTrangThai() == true){
                Nhanvien nv = new Nhanvien();
                nv.setN(n);
                Listnv.add(nv);
            }
        }
        return Listnv;
    }

    public Nhanvien findByMaNguoiDung(Integer id){
        return repo.findByN_MaNguoiDung(id);
    }

    public boolean IsNhanVienTonTai(int id){
        for (Nhanvien nv : repo.findAll()){
            if(nv.n.getMaNguoiDung() == id){
                return true;
            }
        }
        return false;
    }
}
