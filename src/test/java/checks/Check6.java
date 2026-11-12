package checks;
import java.util.Objects;

public final class Check6 {
    private static int count;
    private Check6() { }
    public static void reset() { count = 0; }
    public static void ok(boolean value, String name) {
        if (!value) throw new AssertionError("FAIL: " + name);
        count++;
        System.out.println("PASS: " + name);
    }
    public static void eq(Object actual, Object expected, String name) {
        ok(Objects.equals(actual, expected), name + " [" + actual + "]");
    }
    public static void invalid(Runnable action, String name) {
        boolean thrown = false;
        try { action.run(); } catch (IllegalArgumentException e) { thrown = true; }
        ok(thrown, name);
    }
    public static void done() { System.out.println("TOTAL: " + count + " checks passed"); }
}
