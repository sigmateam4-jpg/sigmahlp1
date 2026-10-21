package com.calculatriceplus.app;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import java.math.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
  LinearLayout root, screen; TextView display, history;
  final CalculatorEngine engine = new CalculatorEngine();
  boolean dark=false;
  int rgb(String c){return Color.parseColor(c);}
  GradientDrawable bg(int top,int bottom,int radius){GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{top,bottom});d.setCornerRadius(radius);d.setStroke(2,dark?rgb("#171a21"):rgb("#707987"));return d;}
  @Override public void onCreate(Bundle b){super.onCreate(b);dark=getPreferences(0).getBoolean("dark",false);build();}
  void render(){
    display.setText(engine.getDisplay());
    StringBuilder s=new StringBuilder();
    java.util.List<String> lines=engine.getHistory();
    int from=Math.max(0,lines.size()-3);
    for(int i=from;i<lines.size();i++)s.append(lines.get(i)).append('\n');
    if(!engine.getPending().isEmpty())s.append(engine.getPending());
    history.setText(s.toString().trim());
  }
  void press(String k){engine.press(k);render();}
  TextView txt(String t,int size,int color){TextView v=new TextView(this);v.setText(t);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);v.setTypeface(Typeface.DEFAULT,Typeface.NORMAL);return v;}
  void build(){root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(12,8,12,12);root.setBackground(bg(dark?rgb("#17191d"):rgb("#e0e3e6"),dark?rgb("#0e1014"):rgb("#c3c8ce"),0));setContentView(root);
   LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);TextView name=txt("CALCULATRICE PLUS",15,dark?Color.WHITE:rgb("#252b32"));header.addView(name,new LinearLayout.LayoutParams(0,50,1));TextView menu=txt("⋮",28,dark?Color.WHITE:rgb("#252b32"));header.addView(menu,new LinearLayout.LayoutParams(48,50));menu.setOnClickListener(v->{PopupMenu pm=new PopupMenu(this,menu);pm.getMenu().add("Thème argenté");pm.getMenu().add("Thème sombre");pm.setOnMenuItemClickListener(item->{dark=item.getTitle().toString().contains("sombre");getPreferences(0).edit().putBoolean("dark",dark).apply();build();return true;});pm.show();});root.addView(header);
   screen=new LinearLayout(this);screen.setOrientation(LinearLayout.VERTICAL);screen.setPadding(12,8,12,8);screen.setBackground(bg(dark?rgb("#303840"):rgb("#a9d8e9"),dark?rgb("#151d24"):rgb("#81b9cf"),12));display=txt("0",40,dark?rgb("#e5f4ff"):rgb("#1d2730"));display.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);display.setMaxLines(1);display.setAutoSizeTextTypeUniformWithConfiguration(14,40,1,android.util.TypedValue.COMPLEX_UNIT_SP);history=txt("",15,dark?rgb("#9eabb7"):rgb("#4b6570"));history.setGravity(Gravity.RIGHT|Gravity.TOP);history.setAutoSizeTextTypeUniformWithConfiguration(9,15,1,android.util.TypedValue.COMPLEX_UNIT_SP);screen.addView(history,new LinearLayout.LayoutParams(-1,0,1));screen.addView(display,new LinearLayout.LayoutParams(-1,0,2));root.addView(screen,new LinearLayout.LayoutParams(-1,0,3));
   String[][] keys={{"mc","m+","m−","mr"},{"C","%","+/−","÷"},{"7","8","9","×"},{"4","5","6","−"},{"1","2","3","+"},{"0",",","⌫","="}};
   for(String[] row:keys){LinearLayout bar=new LinearLayout(this);for(String k:row){boolean mem=k.startsWith("m");boolean num=k.matches("[0-9]")||k.equals(",")||k.equals("⌫");int top,bottom,fg;if(dark){top=mem?rgb("#353b46"):num?rgb("#30343b"):rgb("#485365");bottom=rgb("#171b22");fg=Color.WHITE;}else{top=mem?rgb("#a4b3cd"):num?rgb("#f4f2e8"):rgb("#e4e9f4");bottom=mem?rgb("#677a9a"):num?rgb("#b9bcb4"):rgb("#b3bfd3");fg=rgb("#15191d");}if(k.equals("C")){top=rgb("#ffb4ac");bottom=rgb("#ec6665");}if(k.equals("=")){top=rgb("#ffd28a");bottom=rgb("#ff8a25");}TextView b=txt(k,mem?20:29,fg);b.setBackground(bg(top,bottom,9));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,-1,1);p.setMargins(3,4,3,4);bar.addView(b,p);b.setOnClickListener(v->press(k));}root.addView(bar,new LinearLayout.LayoutParams(-1,0,1));}
   render();}
}
