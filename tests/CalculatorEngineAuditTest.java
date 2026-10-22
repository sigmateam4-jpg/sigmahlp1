import com.calculatriceplus.app.CalculatorEngine;
import java.math.BigDecimal;
import java.util.Arrays;

public class CalculatorEngineAuditTest {
  private static int passed = 0;
  private static CalculatorEngine run(String keys) {
    CalculatorEngine e = new CalculatorEngine();
    for (String key : keys.split(" ")) if (!key.isEmpty()) e.press(key);
    return e;
  }
  private static void display(String keys, String expected) {
    String actual = run(keys).getDisplay();
    if (!expected.equals(actual)) throw new AssertionError(keys + ": expected " + expected + ", actual " + actual);
    passed++;
  }
  private static void memory(String keys, String expected) {
    BigDecimal actual = run(keys).getMemory();
    if (actual.compareTo(new BigDecimal(expected)) != 0)
      throw new AssertionError(keys + ": expected memory " + expected + ", actual " + actual);
    passed++;
  }
  private static void history(String keys, String... expected) {
    if (!run(keys).getHistory().equals(Arrays.asList(expected)))
      throw new AssertionError("History differs for " + keys);
    passed++;
  }
  public static void main(String[] args) {
    display("7 + 8 =", "15");
    display("9 − 4 =", "5");
    display("6 × 7 =", "42");
    display("8 ÷ 2 =", "4");
    display("5 − 8 =", "−3");
    display("0 , 1 + 0 , 2 =", "0,3");
    display("1 , 5 × 2 =", "3");
    display("2 + 3 = =", "8");
    display("2 + 3 = = =", "11");
    display("5 + × 3 =", "15");
    display("2 + 3 + 4 =", "9");
    display("2 0 0 × 1 0 % =", "20");
    display("2 0 0 + 1 0 % =", "220");
    display("2 0 0 − 1 0 % =", "180");
    display("2 0 0 ÷ 1 0 % =", "2 000");
    display("1 0 %", "0,1");
    display("1 ÷ 0 =", "Erreur");
    display("1 ÷ 0 = 5", "5");
    display("1 ÷ 0 = C", "0");
    display("5 , , 5", "5,5");
    display("1 2 3 ⌫", "12");
    display("1 ⌫", "0");
    display("1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6", "1 234 567 890 123 456");
    display("5 m+ 3 m+ mr", "8");
    display("5 m+ 2 m− mr", "3");
    display("5 m+ mc mr", "0");
    display("5 m+ C mr", "5");
    display("9 m+ C 2 × mr =", "18");
    memory("0 , 1 m+ 0 , 2 m+", "0.3");
    memory("5 m+ 2 m−", "3");
    memory("5 m+ mc", "0");
    history("2 + 3 × 4 =", "2 + 3 = 5", "5 × 4 = 20");
    CalculatorEngine fresh = new CalculatorEngine();
    if (!fresh.getHistory().isEmpty() || fresh.getMemory().signum() != 0 || !"0".equals(fresh.getDisplay()))
      throw new AssertionError("New session must start empty");
    passed++;
    CalculatorEngine decimal = run("1 ÷ 3 =");
    decimal.press("m+");
    if (decimal.getMemory().compareTo(new BigDecimal("0.333333333333333333333333")) != 0)
      throw new AssertionError("Internal precision changed");
    passed++;
    System.out.println("PASS: " + passed + " audit regression checks");
  }
}
