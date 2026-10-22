package com.calculatriceplus.app;

import java.math.*;
import java.util.*;

/** Session-only calculator state. No storage of calculations or memory. */
public final class CalculatorEngine implements java.io.Serializable {
  private static final long serialVersionUID = 1L;
  private static final MathContext PRECISION = new MathContext(24, RoundingMode.HALF_UP);
  private String entry = "0", operator = null;
  private BigDecimal left = null, memory = BigDecimal.ZERO;
  private boolean awaitingOperand = false, justEvaluated = false, error = false;
  /** Vrai après m+, m− ou mr : le prochain chiffre démarre un nouveau nombre. */
  private boolean replaceEntry = false;
  /** Dernière opération validée par « = » : permet de répéter « = ». */
  private String lastOperator = null;
  private BigDecimal lastOperand = null;
  private static final int MAX_HISTORY = 100;
  private final ArrayList<String> history = new ArrayList<>();
  private void addHistory(String line) {
    if (history.size() >= MAX_HISTORY) history.remove(0);
    history.add(line);
  }

  /** Affiche le texte saisi tel quel (les zéros finaux de « 1,0 » ou « 0,05 » sont conservés). */
  public String getDisplay() {
    if (error) return "Erreur";
    String plain = entry.replace("-", "");
    if (justEvaluated && plain.length() > 15) return compact(parse(entry));
    return groupText(entry);
  }
  /** Résultat compact et lisible : ne modifie jamais la valeur interne BigDecimal. */
  private static String compact(BigDecimal value) {
    BigDecimal rounded = value.round(new MathContext(12, RoundingMode.HALF_UP)).stripTrailingZeros();
    String plain = groupText(rounded.toPlainString().replace('.', ','));
    if (plain.length() <= 17) return plain;
    return scientificCompact(value);
  }
  private static String scientificCompact(BigDecimal value) {
    BigDecimal rounded = value.round(new MathContext(10, RoundingMode.HALF_UP)).stripTrailingZeros();
    String digits = rounded.unscaledValue().abs().toString();
    int exponent = rounded.precision() - rounded.scale() - 1;
    return (rounded.signum() < 0 ? "−" : "") + digits.charAt(0)
      + (digits.length() > 1 ? "," + digits.substring(1) : "")
      + "E" + (exponent >= 0 ? "+" : "−") + Math.abs(exponent);
  }
  public List<String> getHistory() { return Collections.unmodifiableList(history); }
  public String getPending() { return left == null || operator == null ? "" : format(left) + " " + operator; }
  public BigDecimal getMemory() { return memory; }
  public boolean hasMemory() { return memory.signum() != 0; }
  public void clearHistory() { history.clear(); }
  public boolean isError() { return error; }
  public void clear() { entry="0"; operator=null; left=null; awaitingOperand=false; justEvaluated=false; error=false; replaceEntry=false; lastOperator=null; lastOperand=null; }

  private static BigDecimal parse(String s) { return new BigDecimal(s.replace(',', '.')); }
  private static String raw(BigDecimal v) { return v.stripTrailingZeros().toPlainString().replace('.', ','); }
  /** Texte « -1234,5 » -> « −1 234,5 » (séparateur de milliers, signe moins typographique). */
  private static String groupText(String t) {
    boolean negative = t.startsWith("-");
    if (negative) t = t.substring(1);
    String[] parts = t.split(",", -1);
    StringBuilder grouped = new StringBuilder(parts[0]);
    for (int i = grouped.length() - 3; i > 0; i -= 3) grouped.insert(i, ' ');
    return (negative ? "−" : "") + grouped + (parts.length > 1 ? "," + parts[1] : "");
  }
  /** Notation scientifique lisible pour les très grands / très petits nombres : 1,234E+48. */
  private static String scientific(BigDecimal v) {
    BigDecimal r = v.round(new MathContext(16, RoundingMode.HALF_UP)).stripTrailingZeros();
    String digits = r.unscaledValue().abs().toString();
    int exp = r.precision() - r.scale() - 1;
    String mantissa = digits.charAt(0) + (digits.length() > 1 ? "," + digits.substring(1) : "");
    return (r.signum() < 0 ? "−" : "") + mantissa + "E" + (exp >= 0 ? "+" : "−") + Math.abs(exp);
  }
  public static String format(BigDecimal v) {
    String plain = v.stripTrailingZeros().toPlainString();
    if (plain.replace("-", "").length() > 36) return scientific(v);
    return groupText(plain.replace('.', ','));
  }
  private static BigDecimal compute(BigDecimal a, BigDecimal b, String op) {
    switch(op) {
      case "+": return a.add(b,PRECISION);
      case "−": return a.subtract(b,PRECISION);
      case "×": return a.multiply(b,PRECISION);
      case "÷": return a.divide(b,PRECISION);
      default: throw new IllegalArgumentException(op);
    }
  }
  public void press(String key) {
    if ("C".equals(key)) { clear(); return; }
    if (error) { if(key.matches("[0-9]") || ",".equals(key)) clear(); else return; }
    try {
      if(key.matches("[0-9]")) {
        if(awaitingOperand || justEvaluated || replaceEntry) { entry="0"; awaitingOperand=false; if(justEvaluated){left=null;operator=null;} justEvaluated=false; replaceEntry=false; }
        if(entry.replace("-", "").replace(",", "").length() >= 24) return;
        entry=entry.equals("0")?key:entry.equals("-0")?"-"+key:entry+key;
      } else if(",".equals(key)) {
        if(awaitingOperand || justEvaluated || replaceEntry) { entry="0";awaitingOperand=false;if(justEvaluated){left=null;operator=null;}justEvaluated=false;replaceEntry=false; }
        if(!entry.contains(","))entry+=",";
      } else if("⌫".equals(key)) {
        if(awaitingOperand) return;
        if(replaceEntry){entry="0";replaceEntry=false;return;}
        if(justEvaluated){entry="0";justEvaluated=false;left=null;operator=null;return;}
        entry=entry.length()<=1?"0":entry.substring(0,entry.length()-1);
        if(entry.equals("-")||entry.isEmpty())entry="0";
      } else if("+/−".equals(key)) {
        boolean fresh = justEvaluated || replaceEntry;
        if(awaitingOperand) {entry="0";awaitingOperand=false;}
        if(justEvaluated){justEvaluated=false;left=null;operator=null;}
        entry = entry.startsWith("-") ? entry.substring(1) : "-" + entry;
        replaceEntry = fresh;
      } else if("%".equals(key)) {
        BigDecimal value=awaitingOperand?BigDecimal.ZERO:parse(entry);
        if(left!=null && ("+".equals(operator)||"−".equals(operator))) value=left.multiply(value,PRECISION);
        entry=raw(value.divide(new BigDecimal("100"),PRECISION));
        awaitingOperand=false;justEvaluated=false;replaceEntry=true;
      } else if("mc".equals(key)) { memory=BigDecimal.ZERO;
      } else if("mr".equals(key)) {entry=raw(memory);awaitingOperand=false;if(justEvaluated){left=null;operator=null;}justEvaluated=false;replaceEntry=true;
      } else if("m+".equals(key)) {memory=memory.add(parse(entry),PRECISION);replaceEntry=true;
      } else if("m−".equals(key)) {memory=memory.subtract(parse(entry),PRECISION);replaceEntry=true;
      } else if("=".equals(key)) {
        if(left==null || operator==null) {
          if(justEvaluated && lastOperator!=null) { // « = » répété : refait la dernière opération
            BigDecimal base=parse(entry), result=compute(base,lastOperand,lastOperator);
            addHistory(format(base)+" "+lastOperator+" "+format(lastOperand)+" = "+format(result));
            entry=raw(result);
          }
          return;
        }
        if(awaitingOperand)return;
        BigDecimal right=parse(entry), result=compute(left,right,operator);
        addHistory(format(left)+" "+operator+" "+format(right)+" = "+format(result));
        lastOperator=operator;lastOperand=right;
        entry=raw(result);left=null;operator=null;awaitingOperand=false;justEvaluated=true;replaceEntry=false;
      } else if(Arrays.asList("+","−","×","÷").contains(key)) {
        if(left!=null && operator!=null && !awaitingOperand) {
          BigDecimal right=parse(entry), result=compute(left,right,operator);
          addHistory(format(left)+" "+operator+" "+format(right)+" = "+format(result));
          entry=raw(result);
        }
        left=parse(entry);operator=key;awaitingOperand=true;justEvaluated=false;replaceEntry=false;
      }
    } catch(ArithmeticException | NumberFormatException ex) {
      error=true;lastOperator=null;lastOperand=null;left=null;operator=null;awaitingOperand=false;justEvaluated=false;replaceEntry=false;
    }
  }
}
