package su26sd09.su26sd09.controller;

import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.dto.RoomReviewRequest;
import su26sd09.su26sd09.service.ReviewService;

@Controller
@RequestMapping("/phong")
public class LegacyRoomReviewController {

    private final ReviewService reviewService;

    public LegacyRoomReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/{maPhong}/danh-gia")
    public String submitLegacyRoomReview(@PathVariable int maPhong,
                                         @Valid @ModelAttribute RoomReviewRequest request,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(authentication)) {
            redirectAttributes.addFlashAttribute("roomReviewError", "Vui lòng đăng nhập để gửi đánh giá.");
            return "redirect:/Login";
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("roomReviewDenied", true);
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

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
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