package checks;
import config.Database6;
import dao.*;
import model.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

// Chỉ SELECT: không sửa/xóa dữ liệu demo. Yêu cầu đã nạp seed6.sql.
public class DatabaseTest6 {
    public static void main(String[] args) throws Exception {
        Check6.reset();
        try (Connection con = Database6.open()) {
            System.out.println("DB: " + con.getMetaData().getDatabaseProductName() + " "
                + con.getMetaData().getDatabaseProductVersion());
            FoodDAO6 food = new FoodDAO6(con);
            Check6.eq(food.searchFoodByName("Cơm").size(), 1, "search partial name");
            Check6.eq(food.searchFoodByName("  cƠm  ").get(0).getId(), 1L, "trim and case insensitive");
            Check6.eq(food.searchFoodByName("không có món này").size(), 0, "no result");
            Check6.eq(food.searchFoodByName("%").size(), 0, "percent not wildcard");
            Check6.eq(food.searchFoodByName("_").size(), 0, "underscore not wildcard");
            Check6.eq(food.searchFoodByName("' OR 1=1 --").size(), 0, "SQL injection is data");
            Check6.eq(food.getFoodDetail(1L).orElseThrow().getName(), "Cơm gà", "detail by id");
            Check6.ok(food.getFoodDetail(999999L).isEmpty(), "missing food optional");
            LocalDateTime start = LocalDateTime.of(2026,10,1,0,0), end = LocalDateTime.of(2026,11,1,0,0);
            FoodRevenueStatisticDAO6 stat = new FoodRevenueStatisticDAO6(con);
            List<FoodRevenueStatistic6> rows = stat.getFoodRevenueStatistic(start,end);
            Check6.eq(rows.size(), 3, "three foods in October");
            String[] expected = {"245000","170000","90000"};
            BigDecimal total = BigDecimal.ZERO;
            for (int i=0;i<rows.size();i++) {
                Check6.eq(rows.get(i).getId(), (long)i+1, "revenue order " + i);
                Check6.ok(rows.get(i).getRevenue().compareTo(new BigDecimal(expected[i])) == 0, "food revenue " + i);
                total = total.add(rows.get(i).getRevenue());
            }
            Check6.ok(total.compareTo(new BigDecimal("505000")) == 0, "October total 505000");
            List<OrderDetail6> history = new OrderDetailDAO6(con).getFoodOrderHistory(1L,start,end);
            Check6.eq(history.size(), 3, "repeated orders kept separate");
            Check6.eq(history.get(0).getId(), 1001L, "first order line");
            Check6.eq(history.get(1).getId(), 1003L, "second line same food same order");
            BigDecimal historyTotal = BigDecimal.ZERO;
            for (OrderDetail6 d: history) historyTotal = historyTotal.add(d.getLineAmount());
            Check6.ok(historyTotal.compareTo(rows.get(0).getRevenue()) == 0, "history sum equals food revenue");
            Check6.ok(history.get(0).getUnitPrice().compareTo(new BigDecimal("60000")) == 0, "historical price retained");
            Check6.ok(history.get(0).getFood().getPrice().compareTo(new BigDecimal("65000")) == 0, "current price separate");
            InvoiceDAO6 invoiceDao = new InvoiceDAO6(con);
            Invoice6 invoice = invoiceDao.getInvoice(101L).orElseThrow();
            Check6.eq(invoice.getId(),201L,"invoice 201");
            Check6.eq(invoice.getOrder().getDetails().size(),3,"all invoice lines");
            Check6.eq(invoice.getOrder().getDetails().get(1).getFood().getId(),3L,"other food in same invoice");
            Check6.ok(invoice.getTotalAmount().compareTo(new BigDecimal("210000")) == 0,"invoice total 210000");
            Check6.eq(invoice.getOrder().getTable().getTableNumber(),"B01","table B01");
            Check6.eq(invoice.getPaidBy().getFullName(),"Nhân viên bán hàng","staff name");
            Check6.ok(invoiceDao.getInvoice(107L).isEmpty(),"order without invoice");
            Check6.eq(invoiceDao.getInvoice(103L).orElseThrow().getStatus(),"UNPAID","unpaid invoice loads but excluded in report");
            Check6.ok(con.getAutoCommit(),"restore autocommit after invoice read");
            Check6.eq(stat.getFoodRevenueStatistic(start.minusYears(1),end.minusYears(1)).size(),0,"empty interval");
            Check6.eq(stat.getFoodRevenueStatistic(end,end.plusDays(1)).size(),1,"include first moment November");
            Check6.eq(stat.getFoodRevenueStatistic(end.minusSeconds(1),end).size(),0,"exclude end boundary");
            AccountDAO6 account = new AccountDAO6(con);
            Check6.eq(account.authenticate("customer6","Customer@123").orElseThrow().getRole(),"CUSTOMER","customer login");
            Check6.eq(account.authenticate("manager6","Manager@123").orElseThrow().getRole(),"MANAGER","manager login");
            Check6.ok(account.authenticate("manager6","bad").isEmpty(),"wrong password");
            Check6.ok(account.authenticate("locked6","Locked@123").isEmpty(),"locked account");
            Check6.ok(account.authenticate("' OR 1=1 --","bad").isEmpty(),"login SQL injection");
            String[] tables = {"User6","Account6","Employee6","Customer6","Food6","Combo6","ComboDetail6",
                "DiningTable6","Reservation6","Order6","OrderDetail6","Invoice6","MembershipCard6","Ingredient6",
                "Supplier6","ImportReceipt6","ImportReceiptDetail6"};
            for (String table:tables) {
                try (Statement s=con.createStatement();ResultSet r=s.executeQuery("SELECT COUNT(*) FROM " + table)) {
                    r.next(); Check6.ok(r.getInt(1)>0,"seeded table " + table);
                }
            }
            Check6.done();
        }
    }
}
