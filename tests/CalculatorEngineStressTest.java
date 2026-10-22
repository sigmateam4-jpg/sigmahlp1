import com.calculatriceplus.app.CalculatorEngine;
import java.math.BigDecimal;
import java.util.Random;

public class CalculatorEngineStressTest {
  private static int passed = 0;
  private static void check(boolean ok, String description) {
    if (!ok) throw new AssertionError(description);
    passed++;
  }
  private static CalculatorEngine run(String sequence) {
    CalculatorEngine e = new CalculatorEngine();
    for (String key : sequence.split(" ")) if (!key.isEmpty()) e.press(key);
    return e;
  }
  public static void main(String[] args) {
    CalculatorEngine history = new CalculatorEngine();
    for (int i = 0; i < 250; i++) {
      history.press("1"); history.press("+"); history.press("1"); history.press("=");
    }
    check(history.getHistory().size() == 100, "History must be bounded to 100 entries");
    check("1 + 1 = 2".equals(history.getHistory().get(99)), "Newest history must be retained");
    history.clearHistory();
    check(history.getHistory().isEmpty(), "History clearing");
    Random random = new Random(42);
    String[] ops = {"+", "−", "×", "÷"};
    for (int i = 0; i < 20000; i++) {
      int a = random.nextInt(10000), b = 1 + random.nextInt(9999);
      String op = ops[random.nextInt(ops.length)];
      CalculatorEngine engine = new CalculatorEngine();
      for (char c : Integer.toString(a).toCharArray()) engine.press(String.valueOf(c));
      engine.press(op);
      for (char c : Integer.toString(b).toCharArray()) engine.press(String.valueOf(c));
      engine.press("=");
      BigDecimal expected;
      BigDecimal x = BigDecimal.valueOf(a), y = BigDecimal.valueOf(b);
      switch (op) {
        case "+": expected=x.add(y); break;
        case "−": expected=x.subtract(y); break;
        case "×": expected=x.multiply(y); break;
        default: expected=x.divide(y, new java.math.MathContext(24, java.math.RoundingMode.HALF_UP));
      }
      engine.press("m+");
      check(expected.compareTo(engine.getMemory()) == 0, "Numeric mismatch at iteration " + i);
    }
    String[] keys={"0","1","2","3","4","5","6","7","8","9",",","+","−","×","÷","=","C","⌫","+/−","%","mc","mr","m+","m−"};
    Random fuzz = new Random(7);
    for (int i=0; i<30000; i++) {
      CalculatorEngine engine = new CalculatorEngine();
      for (int j=0; j<30; j++) {
        engine.press(keys[fuzz.nextInt(keys.length)]);
        String value=engine.getDisplay();
        if (value==null || value.isEmpty() || value.contains("NaN") || value.contains("null"))
          throw new AssertionError("Invalid display at fuzz case "+i);
        engine.getPending();
        engine.getHistory();
      }
      passed++;
    }
    CalculatorEngine fresh = run("5 m+ C");
    check(fresh.getMemory().compareTo(BigDecimal.valueOf(5)) == 0, "C must not clear memory");
    System.out.println("PASS: "+passed+" stress assertions (20k numeric, 30k random sequences)");
  }
}
