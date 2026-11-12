package checks;
import model.*;
import util.*;
import web.Html6;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class UnitTest6 {
    public static void main(String[] args) {
        Check6.reset();
        Check6.eq(Validation6.keyword("  Cơm  "), "Cơm", "trim keyword");
        Check6.eq(Validation6.likePattern("Cơm"), "%Cơm%", "LIKE contains");
        Check6.eq(Validation6.likePattern("%_!"), "%!%!_!!%", "escape %, _, !");
        Check6.invalid(() -> Validation6.keyword(" "), "reject empty keyword");
        Check6.invalid(() -> Validation6.keyword(null), "reject null keyword");
        Check6.invalid(() -> Validation6.keyword("a".repeat(151)), "reject long keyword");
        Check6.eq(Validation6.keyword("a".repeat(150)).length(), 150, "accept maximum length");
        Check6.eq(Validation6.id("12"), 12L, "parse positive id");
        Check6.invalid(() -> Validation6.id("0"), "reject zero id");
        Check6.invalid(() -> Validation6.id("-1"), "reject negative id");
        Check6.invalid(() -> Validation6.id("abc"), "reject nonnumeric id");
        Check6.invalid(() -> Validation6.id(null), "reject null id");
        Check6.invalid(() -> Validation6.id("99999999999999999999"), "reject overflowing id");
        Check6.eq(Validation6.dates("2026-10-01", "2026-10-31")[1].plusDays(1).toString(),
            "2026-11-01", "inclusive last day becomes exclusive next day");
        Check6.eq(Validation6.dates("2024-02-29", "2024-02-29")[0].getDayOfMonth(), 29, "leap date");
        Check6.invalid(() -> Validation6.dates("2026-02-29", "2026-03-01"), "invalid date");
        Check6.invalid(() -> Validation6.dates("2026-10-31", "2026-10-01"), "reversed dates");
        Check6.invalid(() -> Validation6.dates(null, "2026-10-01"), "missing date");
        Check6.invalid(() -> Validation6.dates("2026-10-01", "9999-12-31"), "avoid next-day overflow in DB");
        LocalDateTime time = LocalDateTime.of(2026,10,1,0,0);
        Check6.invalid(() -> Validation6.range(time, time), "reject zero time interval");
        Check6.eq(Html6.e("<b>\"&'"), "&lt;b&gt;&quot;&amp;&#39;", "escape HTML");
        Check6.eq(Html6.e(null), "", "null HTML");
        Check6.ok(Html6.url("/restman6", "/x", "q", "a&b").endsWith("q=a%26b"), "URL encoding");
        Check6.eq(Html6.money(new BigDecimal("65000")), "65.000 đ", "Vietnamese money");
        Check6.eq(Html6.money(new BigDecimal("12.25")), "12,25 đ", "do not truncate fraction");
        OrderDetail6 line = new OrderDetail6();
        line.setQuantity(2); line.setUnitPrice(new BigDecimal("60000"));
        Check6.ok(line.getLineAmount().compareTo(new BigDecimal("120000")) == 0, "line amount");
        Order6 order = new Order6(); order.getDetails().add(line);
        OrderDetail6 other = new OrderDetail6(); other.setQuantity(3); other.setUnitPrice(new BigDecimal("30000"));
        order.getDetails().add(other);
        Invoice6 invoice = new Invoice6(); invoice.setOrder(order);
        Check6.ok(invoice.getTotalAmount().compareTo(new BigDecimal("210000")) == 0, "invoice total");
        Check6.ok(new Order6().getTotalAmount().signum() == 0, "empty order total");
        String hash = Password6.hash("Customer@123");
        Check6.ok(Password6.verify("Customer@123", hash), "password verifies");
        Check6.ok(!Password6.verify("wrong", hash), "wrong password rejected");
        Check6.ok(!Password6.verify(null, hash), "null password rejected");
        Check6.ok(!Password6.verify("x", "malformed"), "malformed hash rejected");
        Check6.ok(!hash.equals(Password6.hash("Customer@123")), "random salt");
        Check6.done();
    }
}
