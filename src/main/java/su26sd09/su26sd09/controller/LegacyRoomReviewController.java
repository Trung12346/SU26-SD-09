package su26sd09.su26sd09.controller;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @PostMapping(value = "/{maPhong}/danh-gia", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String submitLegacyRoomReview(@PathVariable int maPhong,
                                         @Valid @ModelAttribute RoomReviewRequest request,
                                         BindingResult bindingResult,
                                         Authentication authentication,
                                         RedirectAttributes redirectAttributes) {
        if (!isLoggedIn(authentication)) {
            return alertAndRedirect("Vui lòng đăng nhập để gửi đánh giá.", "/Login");
        }

        if (bindingResult.hasErrors()) {
            return alertAndRedirect(firstValidationMessage(bindingResult), roomDetailUrl(maPhong));
        }

        try {
            reviewService.createRoomReview(maPhong, authentication.getName(), request);
            redirectAttributes.addFlashAttribute("roomReviewSuccess", "Cảm ơn bạn! Đánh giá đã được gửi thành công.");
            return "<script>window.location.replace('" + roomDetailUrl(maPhong) + "');</script>";
        } catch (IllegalArgumentException ex) {
            return alertAndRedirect(ex.getMessage(), roomDetailUrl(maPhong));
        }
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

    private String alertAndRedirect(String message, String targetUrl) {
        return "<!doctype html><html><head><meta charset=\"UTF-8\"></head><body>"
                + "<script>alert('" + escapeJavaScript(message) + "');"
                + "window.location.replace('" + escapeJavaScript(targetUrl) + "');</script>"
                + "</body></html>";
    }

    private String roomDetailUrl(int maPhong) {
        return "/phong/" + maPhong + "#roomReviews";
    }

    private String escapeJavaScript(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\r", "")
                .replace("\n", "\\n")
                .replace("<", "\\u003C")
                .replace(">", "\\u003E");
    }
}
