package com.calculatriceplus.app;

import android.app.*;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import android.view.*;
import android.widget.*;
import java.util.List;

public class MainActivity extends Activity {
  private CalculatorEngine engine = new CalculatorEngine();
  private LinearLayout root;
  private TextView display, pending, memoryIndicator, historyPreview;
  private HorizontalScrollView displayScroll;
  private boolean dark;
  private boolean showRecentCalculation = false;

  private int color(String value) { return Color.parseColor(value); }
  private int dp(float value) { return (int)(value * getResources().getDisplayMetrics().density + .5f); }
  private int foreground() { return dark ? color("#F3F6FA") : color("#182530"); }
  private int secondary() { return dark ? color("#AEBFCB") : color("#4D6474"); }
  private GradientDrawable surface(int fill, int radius, int border) {
    GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(fill);
    drawable.setCornerRadius(dp(radius));
    if (border != 0) drawable.setStroke(dp(1), border);
    return drawable;
  }
  private TextView text(String label, float sp, int ink) {
    TextView view = new TextView(this);
    view.setText(label);
    view.setTextColor(ink);
    view.setTextSize(TypedValue.COMPLEX_UNIT_SP, sp);
    view.setGravity(Gravity.CENTER);
    view.setIncludeFontPadding(false);
    return view;
  }
  private void accessible(TextView view, String description, View.OnClickListener listener) {
    view.setContentDescription(description);
    view.setClickable(true);
    view.setFocusable(true);
    view.setOnClickListener(listener);
  }
  @Override public void onCreate(Bundle saved) {
    super.onCreate(saved);
    dark = getPreferences(0).getBoolean("dark", false);
    build();
  }
  private void render() {
    display.setText(engine.getDisplay());
    if (displayScroll != null) displayScroll.post(() -> displayScroll.scrollTo(display.getWidth(), 0));
    pending.setText(engine.getPending());
    memoryIndicator.setText(engine.hasMemory() ? "M" : "");
    List<String> lines = engine.getHistory();
    historyPreview.setText(!showRecentCalculation || lines.isEmpty() ? "" : compactHistory(lines.get(lines.size()-1)));
  }
  private String compactHistory(String line) {
    int equals = line.lastIndexOf(" = ");
    if (equals < 0) return line;
    String original = line.substring(equals + 3);
    try {
      java.math.BigDecimal value = new java.math.BigDecimal(
        original.replace(" ", "").replace("−", "-").replace(',', '.'));
      java.math.BigDecimal rounded = value.round(
        new java.math.MathContext(10, java.math.RoundingMode.HALF_UP)).stripTrailingZeros();
      String shortResult = rounded.toPlainString().replace('.', ',').replace("-", "−");
      if (shortResult.length() > 17) {
        int exponent = rounded.precision() - rounded.scale() - 1;
        String digits = rounded.unscaledValue().abs().toString();
        shortResult = (rounded.signum() < 0 ? "−" : "") + digits.charAt(0)
          + (digits.length() > 1 ? "," + digits.substring(1) : "")
          + "E" + (exponent >= 0 ? "+" : "−") + Math.abs(exponent);
      }
      return line.substring(0, equals + 3)
        + (value.compareTo(rounded) == 0 ? "" : "≈ ") + shortResult;
    } catch (NumberFormatException ex) { return line; }
  }
  private void press(String key) {
    int before = engine.getHistory().size();
    engine.press(key);
    showRecentCalculation = engine.getHistory().size() > before;
    render();
  }
  private AlertDialog.Builder dialog() {
    return new AlertDialog.Builder(this, dark ? android.R.style.Theme_Material_Dialog_Alert : android.R.style.Theme_Material_Light_Dialog_Alert);
  }
  private void showHistory() {
    List<String> lines = engine.getHistory();
    if (lines.isEmpty()) {
      dialog().setTitle("Historique")
        .setMessage("Aucun calcul pour cette session.")
        .setPositiveButton("Fermer", null).show();
      return;
    }
    StringBuilder all = new StringBuilder();
    for (String line : lines) all.append(compactHistory(line)).append("\n\n");
    ScrollView scroll = new ScrollView(this);
    TextView content = text(all.toString().trim(), 17, foreground());
    content.setGravity(Gravity.TOP | Gravity.RIGHT);
    content.setPadding(dp(20), dp(18), dp(20), dp(18));
    scroll.addView(content);
    AlertDialog dialog = dialog().setTitle("Historique de la session")
      .setView(scroll).setNeutralButton("Effacer", (d,w) ->
        dialog().setTitle("Effacer l'historique ?")
          .setMessage("Les calculs de cette session seront supprimés.")
          .setNegativeButton("Annuler", null)
          .setPositiveButton("Effacer", (confirm,which) -> { engine.clearHistory(); showRecentCalculation = false; render(); }).show())
      .setPositiveButton("Fermer", null).create();
    dialog.show();
  }
  private void showMenu(View anchor) {
    PopupMenu popup = new PopupMenu(this, anchor);
    popup.getMenu().add("Thème argenté");
    popup.getMenu().add("Thème sombre");
    popup.getMenu().add("Historique");
    popup.setOnMenuItemClickListener(item -> {
      String title = item.getTitle().toString();
      if (title.equals("Historique")) { showHistory(); return true; }
      dark = title.equals("Thème sombre");
      getPreferences(0).edit().putBoolean("dark", dark).apply();
      build();
      return true;
    });
    popup.show();
  }
  private void build() {
    root = new LinearLayout(this);
    root.setOrientation(LinearLayout.VERTICAL);
    root.setPadding(dp(10), dp(4), dp(10), dp(10));
    root.setBackgroundColor(dark ? color("#151A20") : color("#D8DDE2"));
    setContentView(root);

    LinearLayout header = new LinearLayout(this);
    header.setGravity(Gravity.CENTER_VERTICAL);
    TextView title = text("CALCULATRICE PLUS", 15, foreground());
    title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    title.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
    title.setPadding(dp(10), 0, 0, 0);
    header.addView(title, new LinearLayout.LayoutParams(0, dp(48), 1f));
    TextView historyButton = text("◷", 27, secondary());
    header.addView(historyButton, new LinearLayout.LayoutParams(dp(48), dp(48)));
    accessible(historyButton, "Afficher l'historique", v -> showHistory());
    TextView menu = text("⋮", 26, foreground());
    header.addView(menu, new LinearLayout.LayoutParams(dp(48), dp(48)));
    accessible(menu, "Ouvrir les options", v -> showMenu(menu));
    root.addView(header);

    LinearLayout screen = new LinearLayout(this);
    screen.setOrientation(LinearLayout.VERTICAL);
    screen.setPadding(dp(16), dp(10), dp(16), dp(10));
    screen.setBackground(surface(dark ? color("#263641") : color("#A8CBDC"), 13,
      dark ? color("#384C59") : color("#86AABA")));
    LinearLayout topLine = new LinearLayout(this);
    topLine.setGravity(Gravity.CENTER_VERTICAL);
    memoryIndicator = text("", 13, secondary());
    memoryIndicator.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
    topLine.addView(memoryIndicator, new LinearLayout.LayoutParams(dp(32), dp(24)));
    historyPreview = text("", 13, secondary());
    historyPreview.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    historyPreview.setSingleLine(true);
    historyPreview.setEllipsize(android.text.TextUtils.TruncateAt.START);
    topLine.addView(historyPreview, new LinearLayout.LayoutParams(0, dp(24), 1f));
    screen.addView(topLine);
    pending = text("", 15, secondary());
    pending.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    pending.setSingleLine(true);
    pending.setEllipsize(android.text.TextUtils.TruncateAt.START);
    screen.addView(pending, new LinearLayout.LayoutParams(-1, dp(28)));
    display = text("0", 40, foreground());
    display.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
    display.setSingleLine(true);
    display.setHorizontallyScrolling(false);
    display.setTextSize(TypedValue.COMPLEX_UNIT_SP, 40);
    displayScroll = new HorizontalScrollView(this);
    displayScroll.setHorizontalScrollBarEnabled(false);
    displayScroll.setFillViewport(true);
    displayScroll.addView(display, new android.widget.FrameLayout.LayoutParams(-2, -1));
    screen.addView(displayScroll, new LinearLayout.LayoutParams(-1, 0, 1f));
    root.addView(screen, new LinearLayout.LayoutParams(-1, 0, 1.28f));

    String[][] keys = {
      {"mc","m+","m−","mr"},
      {"C","%","+/−","÷"},
      {"7","8","9","×"},
      {"4","5","6","−"},
      {"1","2","3","+"},
      {"0",",","⌫","="}
    };
    for (String[] row : keys) {
      LinearLayout bar = new LinearLayout(this);
      bar.setOrientation(LinearLayout.HORIZONTAL);
      for (String key : row) {
        boolean mem = key.startsWith("m");
        boolean number = key.matches("[0-9]") || key.equals(",") || key.equals("⌫");
        int fill = dark ? (mem ? color("#354453") : number ? color("#292F38") : color("#3A5064"))
          : (mem ? color("#B6C5D3") : number ? color("#F1F3F4") : color("#CBD7E1"));
        int ink = foreground();
        if (key.equals("C")) { fill = dark ? color("#AE5155") : color("#E98B8B"); ink = Color.WHITE; }
        if (key.equals("=")) { fill = dark ? color("#D58B38") : color("#E79A3B"); ink = Color.WHITE; }
        TextView button = text(key, mem ? 19 : 28, ink);
        button.setTypeface(Typeface.DEFAULT, mem ? Typeface.NORMAL : Typeface.BOLD);
        button.setBackground(surface(fill, 11, dark ? color("#414A55") : color("#B8C2CB")));
        LinearLayout.LayoutParams cell = new LinearLayout.LayoutParams(0, -1, 1f);
        cell.setMargins(dp(3), dp(4), dp(3), dp(4));
        bar.addView(button, cell);
        accessible(button, key.equals("⌫") ? "Effacer le dernier chiffre" : key, v -> press(key));
      }
      root.addView(bar, new LinearLayout.LayoutParams(-1, 0, row[0].equals("mc") ? 0.78f : 1f));
    }
    render();
  }
}
