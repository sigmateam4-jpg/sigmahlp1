import com.calculatriceplus.app.CalculatorEngine;
public class CalculatorEngineTest {
  static int passed=0;
  static void check(String expected,String... keys){CalculatorEngine c=new CalculatorEngine();for(String k:keys)c.press(k);if(!expected.equals(c.getDisplay()))throw new AssertionError(java.util.Arrays.toString(keys)+" expected="+expected+" actual="+c.getDisplay());passed++;}
  public static void main(String[] args){
    check("5","2","+","3","=");
    check("15","2","+","3","×","3","=");
    check("−1","2","+","−","3","=");
    check("5","2","+","3","=","+");
    check("7","2","+","3","=","7");
    check("0","2","+","3","=","⌫");
    check("12","1","2",",","⌫");
    check("2,5","1","0","÷","4","=");
    check("Erreur","1","÷","0","=");
    check("3","1","÷","0","=","3");
    check("110","1","0","0","+","1","0","%","=");
    check("10","2","0","0","×","5","%","=");
    check("−5","5","+/−");
    check("5","5","m+","C","mr");
    check("0","5","m+","mc","mr");
    check("8","2","+","3","=","=");
    check("11","2","+","3","=","=","=");
    check("−0,5","+/−",",","5");
    check("2","5","+","3","=","+/−","2");
    check("1 234","1","2","3","4");
    check("0,5",",","5");
    CalculatorEngine c=new CalculatorEngine();c.press("1");c.press("+");c.press("2");c.press("=");if(c.getHistory().size()!=1)throw new AssertionError("history");passed++;
    CalculatorEngine d=new CalculatorEngine();if(!d.getHistory().isEmpty()||d.getMemory().signum()!=0)throw new AssertionError("new session");passed++;
    System.out.println("PASS: "+passed+" calculator engine checks");
  }
}
