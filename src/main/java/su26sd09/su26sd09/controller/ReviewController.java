package su26sd09.su26sd09.controller;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.dto.RoomReviewReplyRequest;
import su26sd09.su26sd09.dto.RoomReviewRequest;
import su26sd09.su26sd09.dto.RoomReviewViewDTO;
import su26sd09.su26sd09.service.ReviewService;

import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/phong/reviews")
public class ReviewController {

    private static final Set<String> REPLY_AUTHORITIES = Set.of("ROLE_STAFF", "ROLE_ADMIN", "ROLE_EMPLOYEE");

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/rooms/{maPhong}/fragment")
    public String roomReviewsFragment(@PathVariable int maPhong, Model model) {
        model.addAttribute("maPhong", maPhong);
        model.addAttribute("roomReviews", reviewService.findApprovedReviewsByRoom(maPhong));
        return "fragments/room-reviews :: roomReviews";
    }

    @GetMapping("/rooms/{maPhong}/data")
    @ResponseBody
    public List<RoomReviewViewDTO> roomReviewsData(@PathVariable int maPhong) {
        return reviewService.findApprovedReviewsByRoom(maPhong);
    }

    @PostMapping({"/rooms", "/rooms/"})
    public String submitRoomReviewMissingRoom(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("roomReviewDenied", true);
        redirectAttributes.addFlashAttribute("roomReviewError", "Không xác định được phòng cần đánh giá. Vui lòng mở lại trang chi tiết phòng rồi thử lại.");
        return "redirect:/phong";
    }

    @PostMapping("/rooms/{maPhong}")
    public String submitRoomReview(@PathVariable int maPhong,
                                   @Valid @ModelAttribute RoomReviewRequest request,
                                   BindingResult bindingResult,
                                   Authentication authentication,
                                   RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(authentication)) {
            redirectAttributes.addFlashAttribute("roomReviewError", "Vui lòng đăng nhập để gửi đánh giá.");
            return "redirect:/Login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("roomReviewError", firstValidationMessage(bindingResult));
            return redirectToRoom(maPhong);
        }

        try {
            reviewService.createRoomReview(maPhong, authentication.getName(), request);
            redirectAttributes.addFlashAttribute("roomReviewSuccess", "Cảm ơn bạn! Đánh giá đã được gửi thành công.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("roomReviewDenied", true);
            redirectAttributes.addFlashAttribute("roomReviewError", ex.getMessage());
        }

        return redirectToRoom(maPhong);
    }

    @PostMapping("/{reviewId}/reply")
    public String replyRoomReview(@PathVariable int reviewId,
                                  @Valid @ModelAttribute RoomReviewReplyRequest request,
                                  BindingResult bindingResult,
                                  Authentication authentication,
                                  RedirectAttributes redirectAttributes) {
        Integer maPhong = request.getMaPhong();
        if (maPhong == null) {
            maPhong = reviewService.findRoomIdByReviewId(reviewId);
        }

        if (!canReply(authentication)) {
            redirectAttributes.addFlashAttribute("roomReviewError", "Bạn không có quyền phản hồi đánh giá.");
            return maPhong != null ? redirectToRoom(maPhong) : "redirect:/phong";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("roomReviewError", firstValidationMessage(bindingResult));
            return maPhong != null ? redirectToRoom(maPhong) : "redirect:/phong";
        }

        try {
            reviewService.replyToReview(reviewId, request);
            redirectAttributes.addFlashAttribute("roomReviewSuccess", "Đã lưu phản hồi đánh giá.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("roomReviewError", ex.getMessage());
        }

        return maPhong != null ? redirectToRoom(maPhong) : "redirect:/phong";
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private boolean canReply(Authentication authentication) {
        return isLoggedIn(authentication)
                && authentication.getAuthorities()
                .stream()
                .anyMatch(authority -> REPLY_AUTHORITIES.contains(authority.getAuthority()));
    }

    private String firstValidationMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors()
                .stream()
                .findFirst()
                .map(FieldError::getDefaultMessage)
                .orElse("Dữ liệu chưa hợp lệ.");
    }

    private String redirectToRoom(int maPhong) {
        return "redirect:/phong/" + maPhong + "#roomReviews";
    }
}
