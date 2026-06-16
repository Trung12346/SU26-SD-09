package su26sd09.su26sd09.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import su26sd09.su26sd09.entity.ChiTietDatPhong;
import su26sd09.su26sd09.entity.DatPhong;
import su26sd09.su26sd09.entity.NguoiDung;
import su26sd09.su26sd09.entity.Phong;
import su26sd09.su26sd09.entity.VaiTro;
import su26sd09.su26sd09.repository.NguoiDungRepository;
import su26sd09.su26sd09.repository.VaiTroRepo;
import su26sd09.su26sd09.service.ChiTietDatPhongService;
import su26sd09.su26sd09.service.DatPhongService;
import su26sd09.su26sd09.service.PhongService;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
public class GuestBookingController {

    private static final String BOOKING_DRAFT_KEY = "guestBookingDraft";
    private static final String GUEST_INFO_KEY = "guestBookingInfo";
    private static final String GUEST_INFO_ROUTE = "/phong/dat-phong/thong-tin-khach";
    private static final String CONFIRM_ROUTE = "/phong/dat-phong/xac-nhan";
    private static final String DEFAULT_ROOM_IMAGE =
            "https://images.unsplash.com/photo-1611892440504-42a792e24d32?auto=format&fit=crop&w=400&q=80";

    @Autowired
    private PhongService phongService;

    @Autowired
    private DatPhongService datPhongService;

    @Autowired
    private ChiTietDatPhongService chiTietDatPhongService;

    @Autowired
    private NguoiDungRepository nguoiDungRepository;

    @Autowired
    private VaiTroRepo vaiTroRepo;

    @GetMapping("/gio-hang")
    public String showCart() {
        return "gio-hang";
    }

    @PostMapping({"/phong/dat-phong", "/phong/dat-phong/quick"})
    public String startBooking(
            @RequestParam(value = "maPhong", required = false) Integer maPhong,
            @RequestParam(value = "ngayNhan", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhan,
            @RequestParam(value = "ngayTra", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayTra,
            @RequestParam(value = "nguoiLon", defaultValue = "2") Integer nguoiLon,
            @RequestParam(value = "treEm", defaultValue = "0") Integer treEm,
            @RequestParam(value = "yeuCauThem", required = false) String yeuCauThem,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        List<Integer> roomIds = cleanRoomIds(maPhong == null ? List.of() : List.of(maPhong));
        return prepareBooking(roomIds, ngayNhan, ngayTra, nguoiLon, treEm, yeuCauThem, session, authentication, redirectAttributes);
    }

    @PostMapping("/gio-hang/checkout")
    public String checkoutCart(
            @RequestParam(value = "roomIds", required = false) List<Integer> roomIds,
            @RequestParam(value = "ngayNhan", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayNhan,
            @RequestParam(value = "ngayTra", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate ngayTra,
            @RequestParam(value = "nguoiLon", defaultValue = "2") Integer nguoiLon,
            @RequestParam(value = "treEm", defaultValue = "0") Integer treEm,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        return prepareBooking(cleanRoomIds(roomIds), ngayNhan, ngayTra, nguoiLon, treEm, null, session, authentication, redirectAttributes);
    }

    @GetMapping("/phong/dat-phong/thong-tin-khach")
    public String showGuestInfo(
            Model model,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        BookingDraft draft = getDraft(session);
        if (draft == null || draft.getRoomIds().isEmpty()) {
            redirectAttributes.addFlashAttribute("bookingError", "Vui lòng chọn phòng trước khi nhập thông tin khách.");
            return "redirect:/phong";
        }
        if (isLoggedIn(authentication)) {
            return redirectTo(CONFIRM_ROUTE);
        }

        addBookingModel(model, draft);
        model.addAttribute("guestInfo", getGuestInfo(session));
        return "dat-phong-thong-tin-khach";
    }

    @PostMapping("/phong/dat-phong/thong-tin-khach")
    public String saveGuestInfo(
            @RequestParam(value = "danhXung", defaultValue = "Ông") String danhXung,
            @RequestParam("ten") String ten,
            @RequestParam("ho") String ho,
            @RequestParam("email") String email,
            @RequestParam("emailNhapLai") String emailNhapLai,
            @RequestParam("soDienThoai") String soDienThoai,
            @RequestParam(value = "quocGia", defaultValue = "Việt Nam") String quocGia,
            @RequestParam(value = "yeuCauThem", required = false) String yeuCauThem,
            HttpSession session,
            RedirectAttributes redirectAttributes
    ) {
        BookingDraft draft = getDraft(session);
        if (draft == null || draft.getRoomIds().isEmpty()) {
            redirectAttributes.addFlashAttribute("bookingError", "Phiên đặt phòng đã hết hạn. Vui lòng chọn phòng lại.");
            return "redirect:/phong";
        }

        String normalizedEmail = email == null ? "" : email.trim();
        String normalizedConfirmEmail = emailNhapLai == null ? "" : emailNhapLai.trim();
        if (!normalizedEmail.equalsIgnoreCase(normalizedConfirmEmail)) {
            redirectAttributes.addFlashAttribute("bookingError", "Email nhập lại chưa khớp.");
            return redirectTo(GUEST_INFO_ROUTE);
        }

        String fullName = buildFullName(danhXung, ho, ten);
        if (fullName.isBlank() || normalizedEmail.isBlank() || soDienThoai == null || soDienThoai.trim().isBlank()) {
            redirectAttributes.addFlashAttribute("bookingError", "Vui lòng nhập đầy đủ tên, email và số điện thoại.");
            return redirectTo(GUEST_INFO_ROUTE);
        }

        session.setAttribute(GUEST_INFO_KEY, new GuestBookingInfo(
                fullName,
                normalizedEmail,
                soDienThoai.trim(),
                quocGia == null || quocGia.isBlank() ? "Việt Nam" : quocGia,
                yeuCauThem
        ));
        return redirectTo(CONFIRM_ROUTE);
    }

    @GetMapping("/phong/dat-phong/xac-nhan")
    public String showConfirm(
            Model model,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        BookingDraft draft = getDraft(session);
        if (draft == null || draft.getRoomIds().isEmpty()) {
            redirectAttributes.addFlashAttribute("bookingError", "Vui lòng chọn phòng trước khi xác nhận đặt phòng.");
            return "redirect:/phong";
        }
        boolean loggedIn = isLoggedIn(authentication);
        if (!loggedIn && getGuestInfo(session) == null) {
            return redirectTo(GUEST_INFO_ROUTE);
        }

        addBookingModel(model, draft);
        model.addAttribute("loggedIn", loggedIn);
        model.addAttribute("guestInfo", loggedIn
                ? toGuestInfo(getAuthenticatedUser(authentication))
                : getGuestInfo(session));
        return "dat-phong-xac-nhan";
    }

    @Transactional
    @PostMapping("/phong/dat-phong/xac-nhan")
    public String confirmBooking(
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        BookingDraft draft = getDraft(session);
        if (draft == null || draft.getRoomIds().isEmpty()) {
            redirectAttributes.addFlashAttribute("bookingError", "Phiên đặt phòng đã hết hạn. Vui lòng chọn phòng lại.");
            return "redirect:/phong";
        }

        String error = validateDraft(draft.getRoomIds(), draft.getNgayNhan(), draft.getNgayTra());
        if (error != null) {
            redirectAttributes.addFlashAttribute("bookingError", error);
            return redirectTo(CONFIRM_ROUTE);
        }

        NguoiDung nguoiDung;
        GuestBookingInfo guestInfo = getGuestInfo(session);
        if (isLoggedIn(authentication)) {
            nguoiDung = getAuthenticatedUser(authentication);
            if (nguoiDung == null) {
                redirectAttributes.addFlashAttribute("bookingError", "Không tìm thấy tài khoản khách hàng đang đăng nhập.");
                return redirectTo(CONFIRM_ROUTE);
            }
        } else if (guestInfo != null) {
            try {
                nguoiDung = findOrCreateGuest(guestInfo);
            } catch (RuntimeException ex) {
                redirectAttributes.addFlashAttribute("bookingError", ex.getMessage());
                return redirectTo(GUEST_INFO_ROUTE);
            }
        } else {
            return redirectTo(GUEST_INFO_ROUTE);
        }

        DatPhong savedDatPhong = createBooking(draft, nguoiDung, firstNonBlank(
                guestInfo == null ? null : guestInfo.getYeuCauThem(),
                draft.getYeuCauThem()
        ));
        session.removeAttribute(BOOKING_DRAFT_KEY);
        session.removeAttribute(GUEST_INFO_KEY);

        redirectAttributes.addFlashAttribute("bookingSuccess",
                "Đặt phòng thành công. Mã đặt phòng của bạn là #" + savedDatPhong.getId() + ".");
        return "redirect:/gio-hang";
    }

    private String prepareBooking(
            List<Integer> roomIds,
            LocalDate ngayNhan,
            LocalDate ngayTra,
            Integer nguoiLon,
            Integer treEm,
            String yeuCauThem,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        String error = validateDraft(roomIds, ngayNhan, ngayTra);
        if (error != null) {
            redirectAttributes.addFlashAttribute("bookingError", error);
            return "redirect:/phong";
        }

        session.setAttribute(BOOKING_DRAFT_KEY,
                new BookingDraft(roomIds, ngayNhan, ngayTra, safeGuestCount(nguoiLon, 1), safeGuestCount(treEm, 0), cleanText(yeuCauThem)));
        session.removeAttribute(GUEST_INFO_KEY);

        return isLoggedIn(authentication)
                ? redirectTo(CONFIRM_ROUTE)
                : redirectTo(GUEST_INFO_ROUTE);
    }

    private String redirectTo(String path) {
        return "redirect:" + path;
    }

    private DatPhong createBooking(BookingDraft draft, NguoiDung nguoiDung, String yeuCauThem) {
        LocalDateTime now = LocalDateTime.now();
        DatPhong datPhong = new DatPhong();
        datPhong.setN(nguoiDung);
        datPhong.setNgaydatPhong(draft.getNgayNhan().atTime(LocalTime.of(14, 0)));
        datPhong.setNgaytraPhong(draft.getNgayTra().atTime(LocalTime.of(12, 0)));
        datPhong.setSonguoiLon(draft.getNguoiLon());
        datPhong.setSotreEm(draft.getTreEm());
        datPhong.setYeuCauThem(yeuCauThem);
        datPhong.setTrangThai("Cho xac nhan");
        datPhong.setNgayTao(now);
        datPhong.setNgayCapNhat(now);

        DatPhong savedDatPhong = datPhongService.save(datPhong);
        for (Integer roomId : draft.getRoomIds()) {
            Phong phong = phongService.findPhongById(roomId);
            if (phong == null) {
                continue;
            }
            ChiTietDatPhong chiTiet = new ChiTietDatPhong();
            chiTiet.setD(savedDatPhong);
            chiTiet.setP(phong);
            chiTiet.setGiaMoiDem(phong.getGiaMoiDem());
            chiTiet.setGiaKhiDat(phong.getGiaMoiDem().multiply(BigDecimal.valueOf(getNightCount(draft))));
            chiTietDatPhongService.save(chiTiet);
            phongService.updateTrangThai(phong.getMaPhong(), "Da dat");
        }
        return savedDatPhong;
    }

    private NguoiDung findOrCreateGuest(GuestBookingInfo guestInfo) {
        NguoiDung sameEmail = nguoiDungRepository.findByEmail(guestInfo.getEmail());
        NguoiDung samePhone = nguoiDungRepository.findBySoDienThoai(guestInfo.getSoDienThoai());

        if (sameEmail != null && samePhone != null
                && !sameEmail.getMaNguoiDung().equals(samePhone.getMaNguoiDung())) {
            throw new RuntimeException("Email và số điện thoại đang thuộc hai khách hàng khác nhau.");
        }
        if (samePhone != null && sameEmail == null) {
            throw new RuntimeException("Số điện thoại đã được dùng với email khác.");
        }

        NguoiDung nguoiDung = sameEmail;
        if (nguoiDung == null) {
            nguoiDung = new NguoiDung();
            nguoiDung.setEmail(guestInfo.getEmail());
            nguoiDung.setMatKhau_hash("GUEST-" + UUID.randomUUID());
            nguoiDung.setVaiTro(findCustomerRole());
            nguoiDung.setTrangThai(true);
        }

        nguoiDung.setHoTen(guestInfo.getHoTen());
        nguoiDung.setSoDienThoai(guestInfo.getSoDienThoai());
        nguoiDung.setDiaChi(guestInfo.getQuocGia());
        return nguoiDungRepository.save(nguoiDung);
    }

    private NguoiDung getAuthenticatedUser(Authentication authentication) {
        if (!isLoggedIn(authentication)) {
            return null;
        }
        return nguoiDungRepository.findByEmail(authentication.getName());
    }

    private boolean isLoggedIn(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private VaiTro findCustomerRole() {
        return vaiTroRepo.findById(3)
                .orElseGet(() -> vaiTroRepo.findAll().stream()
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Chưa có vai trò khách hàng trong cơ sở dữ liệu.")));
    }

    private GuestBookingInfo toGuestInfo(NguoiDung nguoiDung) {
        if (nguoiDung == null) {
            return null;
        }
        return new GuestBookingInfo(
                nguoiDung.getHoTen(),
                nguoiDung.getEmail(),
                nguoiDung.getSoDienThoai(),
                nguoiDung.getDiaChi(),
                null
        );
    }

    private String buildFullName(String danhXung, String ho, String ten) {
        String prefix = danhXung == null ? "" : danhXung.trim();
        String lastName = ho == null ? "" : ho.trim();
        String firstName = ten == null ? "" : ten.trim();
        String name = (lastName + " " + firstName).trim();
        return (prefix + " " + name).trim();
    }

    private void addBookingModel(Model model, BookingDraft draft) {
        List<Phong> rooms = loadRooms(draft);
        long nightCount = getNightCount(draft);
        BigDecimal subtotal = rooms.stream()
                .map(Phong::getGiaMoiDem)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal total = subtotal.multiply(BigDecimal.valueOf(Math.max(nightCount, 1)));

        model.addAttribute("cartRooms", rooms.stream().map(this::toRoomMap).collect(Collectors.toList()));
        model.addAttribute("roomCount", rooms.size());
        model.addAttribute("nightCount", nightCount);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("total", total);
        model.addAttribute("ngayNhan", draft == null ? null : draft.getNgayNhan());
        model.addAttribute("ngayTra", draft == null ? null : draft.getNgayTra());
        model.addAttribute("nguoiLon", draft == null ? 2 : draft.getNguoiLon());
        model.addAttribute("treEm", draft == null ? 0 : draft.getTreEm());
        model.addAttribute("yeuCauThem", draft == null ? null : draft.getYeuCauThem());
    }

    private List<Phong> loadRooms(BookingDraft draft) {
        if (draft == null || draft.getRoomIds() == null) {
            return List.of();
        }
        List<Phong> rooms = new ArrayList<>();
        for (Integer roomId : draft.getRoomIds()) {
            Phong phong = phongService.findPhongById(roomId);
            if (phong != null) {
                rooms.add(phong);
            }
        }
        return rooms;
    }

    private Map<String, Object> toRoomMap(Phong phong) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("id", phong.getMaPhong());
        item.put("roomNumber", phong.getSoPhong());
        item.put("floor", phong.getSoTang());
        item.put("price", phong.getGiaMoiDem());
        item.put("typeName", phong.getLoaiPhong() == null ? "" : phong.getLoaiPhong().getTenLoai());
        item.put("image", DEFAULT_ROOM_IMAGE);
        return item;
    }

    private BookingDraft getDraft(HttpSession session) {
        Object draft = session.getAttribute(BOOKING_DRAFT_KEY);
        return draft instanceof BookingDraft ? (BookingDraft) draft : null;
    }

    private GuestBookingInfo getGuestInfo(HttpSession session) {
        Object guestInfo = session.getAttribute(GUEST_INFO_KEY);
        return guestInfo instanceof GuestBookingInfo ? (GuestBookingInfo) guestInfo : null;
    }

    private List<Integer> cleanRoomIds(List<Integer> roomIds) {
        if (roomIds == null) {
            return List.of();
        }
        return roomIds.stream()
                .filter(Objects::nonNull)
                .filter(id -> id > 0)
                .distinct()
                .collect(Collectors.toList());
    }

    private String validateDraft(List<Integer> roomIds, LocalDate ngayNhan, LocalDate ngayTra) {
        if (roomIds == null || roomIds.isEmpty()) {
            return "Vui lòng chọn ít nhất một phòng.";
        }
        if (ngayNhan == null || ngayTra == null) {
            return "Vui lòng chọn ngày nhận và ngày trả phòng.";
        }
        if (!ngayTra.isAfter(ngayNhan)) {
            return "Ngày trả phòng phải sau ngày nhận phòng.";
        }
        for (Integer roomId : roomIds) {
            Phong phong = phongService.findPhongById(roomId);
            if (phong == null || !phong.isHoatDong()) {
                return "Có phòng không còn hoạt động. Vui lòng kiểm tra lại danh sách.";
            }
            if (!"Trong".equals(phong.getTrangThai())) {
                return "Có phòng không còn trống. Vui lòng kiểm tra lại danh sách.";
            }
        }
        return null;
    }

    private long getNightCount(BookingDraft draft) {
        if (draft == null || draft.getNgayNhan() == null || draft.getNgayTra() == null) {
            return 0;
        }
        return Math.max(draft.getNgayTra().toEpochDay() - draft.getNgayNhan().toEpochDay(), 0);
    }

    private int safeGuestCount(Integer value, int fallback) {
        return value == null || value < 0 ? fallback : value;
    }

    private String cleanText(String value) {
        if (value == null || value.trim().isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String firstNonBlank(String primary, String fallback) {
        return cleanText(primary) != null ? cleanText(primary) : cleanText(fallback);
    }

    public static class BookingDraft implements Serializable {
        private final List<Integer> roomIds;
        private final LocalDate ngayNhan;
        private final LocalDate ngayTra;
        private final int nguoiLon;
        private final int treEm;
        private final String yeuCauThem;

        public BookingDraft(List<Integer> roomIds, LocalDate ngayNhan, LocalDate ngayTra, int nguoiLon, int treEm, String yeuCauThem) {
            this.roomIds = roomIds;
            this.ngayNhan = ngayNhan;
            this.ngayTra = ngayTra;
            this.nguoiLon = nguoiLon;
            this.treEm = treEm;
            this.yeuCauThem = yeuCauThem;
        }

        public List<Integer> getRoomIds() {
            return roomIds;
        }

        public LocalDate getNgayNhan() {
            return ngayNhan;
        }

        public LocalDate getNgayTra() {
            return ngayTra;
        }

        public int getNguoiLon() {
            return nguoiLon;
        }

        public int getTreEm() {
            return treEm;
        }

        public String getYeuCauThem() {
            return yeuCauThem;
        }
    }

    public static class GuestBookingInfo implements Serializable {
        private final String hoTen;
        private final String email;
        private final String soDienThoai;
        private final String quocGia;
        private final String yeuCauThem;

        public GuestBookingInfo(String hoTen, String email, String soDienThoai, String quocGia, String yeuCauThem) {
            this.hoTen = hoTen;
            this.email = email;
            this.soDienThoai = soDienThoai;
            this.quocGia = quocGia;
            this.yeuCauThem = yeuCauThem;
        }

        public String getHoTen() {
            return hoTen;
        }

        public String getEmail() {
            return email;
        }

        public String getSoDienThoai() {
            return soDienThoai;
        }

        public String getQuocGia() {
            return quocGia;
        }

        public String getYeuCauThem() {
            return yeuCauThem;
        }
    }
}
