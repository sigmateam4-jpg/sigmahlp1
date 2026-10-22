import com.calculatriceplus.app.CalculatorEngine;
import java.math.BigDecimal;
public class CalculatorEngineV23Test {
  private static int count=0;
  static CalculatorEngine run(String keys) {
    CalculatorEngine e=new CalculatorEngine();
    for(String key:keys.split(" ")) e.press(key);
    return e;
  }
  static void display(String keys,String expected) {
    String actual=run(keys).getDisplay();
    if(!expected.equals(actual)) throw new AssertionError(keys+" expected "+expected+" got "+actual);
    count++;
  }
  static void numeric(String keys,String expected) {
    CalculatorEngine e=run(keys);
    e.press("m+");
    if(e.getMemory().compareTo(new BigDecimal(expected))!=0)
      throw new AssertionError(keys+" precision lost: "+e.getMemory());
    count++;
  }
  public static void main(String[] args) {
    display("7 + 8 =", "15");
    display("2 + 3 = =", "8");
    display("1 ÷ 0 =", "Erreur");
    display("1 ÷ 0 = 5", "5");
    display("2 0 0 + 1 0 % =", "220");
    display("2 0 0 − 1 0 % =", "180");
    display("2 0 0 × 1 0 % =", "20");
    display("2 0 0 ÷ 1 0 % =", "2 000");
    display("5 m+ 3 m+ mr", "8");
    display("5 m+ mc mr", "0");
    display("1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6", "1 234 567 890 123 456");
    display("1 ÷ 3 =", "0,333333333333");
    numeric("1 ÷ 3 =", "0.333333333333333333333333");
    numeric("7 8 ÷ 4 , 2 =", "18.57142857142857142857143");
    CalculatorEngine e=run("5 + 3 =");
    if(e.getHistory().size()!=1) throw new AssertionError("history");
    count++;
    System.out.println("PASS: "+count+" V2.3 regression tests");
  }
}
