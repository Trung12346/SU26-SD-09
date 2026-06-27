package su26sd09.su26sd09.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.dto.CustomerReviewRequest;
import su26sd09.su26sd09.dto.CustomerReviewViewDTO;
import su26sd09.su26sd09.entity.DanhGia;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.repository.DanhGiaRepo;
import su26sd09.su26sd09.repository.DatPhongRepo;
import su26sd09.su26sd09.repository.NguoiDungRepository;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/home/reviews")
public class CustomerReviewController {

    private final DanhGiaRepo danhGiaRepo;
    private final DatPhongRepo datPhongRepo;
    private final NguoiDungRepository nguoiDungRepository;

    public CustomerReviewController(DanhGiaRepo danhGiaRepo,
                                    DatPhongRepo datPhongRepo,
                                    NguoiDungRepository nguoiDungRepository) {
        this.danhGiaRepo = danhGiaRepo;
        this.datPhongRepo = datPhongRepo;
        this.nguoiDungRepository = nguoiDungRepository;
    }

    @GetMapping
    public String reviewPage(Model model) {
        addReviewModel(model);
        return "customer-reviews-page";
    }

    @GetMapping("/fragment")
    public String reviewFragment(Model model) {
        addReviewModel(model);
        return "fragments/customer-reviews :: customerReviews";
    }

    @GetMapping("/data")
    @ResponseBody
    public List<CustomerReviewViewDTO> reviewData() {
        return findApprovedReviews();
    }

    @PostMapping
    public String submitReview(@Valid @ModelAttribute CustomerReviewRequest request,
                               BindingResult bindingResult,
                               Principal principal,
                               RedirectAttributes redirectAttributes) {
        if (principal == null) {
            redirectAttributes.addFlashAttribute("reviewError", "Vui lòng đăng nhập để gửi đánh giá.");
            return "redirect:/Login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("reviewError", firstValidationMessage(bindingResult));
            return "redirect:/home/reviews";
        }

        NguoiDung nguoiDung = nguoiDungRepository.findByEmail(principal.getName());
        if (nguoiDung == null) {
            redirectAttributes.addFlashAttribute("reviewError", "Không tìm thấy tài khoản đăng nhập.");
            return "redirect:/Login";
        }

        DatPhong datPhong = null;
        if (request.getMaDatPhong() != null) {
            datPhong = datPhongRepo.findById(request.getMaDatPhong()).orElse(null);
            if (!isOwner(datPhong, nguoiDung)) {
                redirectAttributes.addFlashAttribute("reviewError", "Mã đặt phòng không hợp lệ với tài khoản hiện tại.");
                return "redirect:/home/reviews";
            }
        }

        DanhGia danhGia = new DanhGia();
        danhGia.setN(nguoiDung);
        danhGia.setD(datPhong);
        danhGia.setDiemDanhGia(Math.max(1, Math.min(5, request.getDiemDanhGia())));
        danhGia.setNoiDung(request.getNoiDung().trim());
        danhGia.setDaDuyet(false);
        danhGia.setNgayTao(LocalDateTime.now());
        danhGiaRepo.save(danhGia);

        redirectAttributes.addFlashAttribute("reviewSuccess", "Cảm ơn bạn! Đánh giá đã được gửi và đang chờ duyệt.");
        return "redirect:/home/reviews";
    }

    private void addReviewModel(Model model) {
        if (!model.containsAttribute("customerReviews")) {
            model.addAttribute("customerReviews", findApprovedReviews());
        }
        if (!model.containsAttribute("reviewForm")) {
            model.addAttribute("reviewForm", new CustomerReviewRequest());
        }
    }

    private List<CustomerReviewViewDTO> findApprovedReviews() {
        return danhGiaRepo.findAll()
                .stream()
                .filter(DanhGia::isDaDuyet)
                .sorted(Comparator.comparing(DanhGia::getNgayTao, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(CustomerReviewViewDTO::fromEntity)
                .toList();
    }

    private boolean isOwner(DatPhong datPhong, NguoiDung nguoiDung) {
        return datPhong != null
                && datPhong.getN() != null
                && datPhong.getN().getMaNguoiDung() != null
                && datPhong.getN().getMaNguoiDung().equals(nguoiDung.getMaNguoiDung());
    }

    private String firstValidationMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dữ liệu đánh giá chưa hợp lệ.");
    }
}
