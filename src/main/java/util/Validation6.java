package util;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public final class Validation6 {
    private Validation6() { }

    public static String keyword(String input) {
        String value = input == null ? "" : input.trim();
        if (value.isEmpty() || value.length() > 150)
            throw new IllegalArgumentException("Nhập tên món ăn từ 1 đến 150 ký tự.");
        return value;
    }

    // % và _ trong từ khóa là ký tự thật, không phải ký tự đại diện SQL.
    public static String likePattern(String input) {
        return "%" + keyword(input).replace("!", "!!").replace("%", "!%")
            .replace("_", "!_") + "%";
    }

    public static void positive(Long id) {
        if (id == null || id <= 0) throw new IllegalArgumentException("Mã phải là số nguyên dương.");
    }

    public static Long id(String input) {
        try {
            Long value = Long.valueOf(input);
            positive(value);
            return value;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Mã phải là số nguyên dương.");
        }
    }

    public static LocalDate[] dates(String start, String end) {
        if (start == null || end == null || start.isBlank() || end.isBlank())
            throw new IllegalArgumentException("Chọn đủ ngày bắt đầu và ngày kết thúc.");
        try {
            LocalDate a = LocalDate.parse(start), b = LocalDate.parse(end);
            if (a.isAfter(b) || a.getYear() < 1000 || b.getYear() > 9998)
                throw new IllegalArgumentException("Khoảng ngày không hợp lệ.");
            return new LocalDate[] {a, b};
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Ngày không hợp lệ, dùng định dạng yyyy-MM-dd.");
        }
    }

    public static void range(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null || !start.isBefore(end))
            throw new IllegalArgumentException("Thời gian bắt đầu phải trước thời gian kết thúc.");
    }
}
