import com.calculatriceplus.app.CalculatorEngine;
import java.util.*;
public class CalculatorEngineExtendedTest {
  static int pass=0, fail=0, info=0;
  static CalculatorEngine run(String keys){ CalculatorEngine c=new CalculatorEngine(); for(String k:keys.trim().split(" +")) if(!k.isEmpty()) c.press(k); return c; }
  static void t(String name,String keys,String exp){
    CalculatorEngine c=run(keys); String a=c.getDisplay();
    boolean ok=exp.equals(a); if(ok)pass++; else fail++;
    System.out.println((ok?"[OK]   ":"[ECHEC]")+" "+name+" | "+keys+" | attendu="+exp+" | obtenu="+a);
  }
  static void i(String name,String keys){ CalculatorEngine c=run(keys); info++; System.out.println("[INFO] "+name+" | "+keys+" | obtenu="+c.getDisplay()); }
  static void h(String name,String keys,int n){ CalculatorEngine c=run(keys); boolean ok=c.getHistory().size()==n; if(ok)pass++;else fail++; System.out.println((ok?"[OK]   ":"[ECHEC]")+" "+name+" | historique="+c.getHistory()+" attendu "+n+" lignes"); }
  public static void main(String[] x){
    System.out.println("== Operations de base");
    t("addition","7 + 8 =","15"); t("soustraction","9 − 4 =","5"); t("multiplication","6 × 7 =","42"); t("division","8 ÷ 2 =","4");
    t("resultat negatif","5 − 8 =","−3");
    System.out.println("== Decimaux");
    t("0,1+0,2","0 , 1 + 0 , 2 =","0,3"); t("1,5x2","1 , 5 × 2 =","3"); t("0,1x3","0 , 1 × 3 =","0,3");
    t("1/3","1 ÷ 3 =","0,333333333333333333333333"); t("2/3 arrondi","2 ÷ 3 =","0,666666666666666666666667");
    t("100/8","1 0 0 ÷ 8 =","12,5"); t("1/3x3","1 ÷ 3 × 3 =","0,999999999999999999999999");
    t("petit x petit","0 , 0 0 0 0 0 0 1 × 0 , 0 0 0 0 0 0 1 =","0,00000000000001");
    System.out.println("== Affichage / saisie");
    t("groupes milliers","1 0 0 0 0 0 0","1 000 000"); t("decimal+milliers","1 2 3 4 5 , 6 7","12 345,67");
    t("zeros initiaux","0 0 0 5","5"); t("double virgule","5 , , 5","5,5"); t("virgule seule","0 ,","0,");
    t("1,0 (zero final)","1 , 0","1,0"); t("0,0 (zero final)","0 , 0","0,0"); t("0,05","0 , 0 5","0,05"); t("1,50","1 , 5 0","1,50");
    t("1,50+1","1 , 5 0 + 1 =","2,5");
    t("limite 24 chiffres","1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1","111 111 111 111 111 111 111 111");
    System.out.println("== Enchainements");
    t("2+3+4","2 + 3 + 4 =","9"); t("10-2-3","1 0 − 2 − 3 =","5"); t("2x3+4","2 × 3 + 4 =","10");
    t("changement op +→×","5 + × 3 =","15"); t("changement op ×→+","5 × + 3 =","8");
    t("continuer apres =","2 × 3 = × 4 =","24"); t("saisie apres = = nouveau calcul","2 + 3 = 4 + 1 =","5");
    t("operateur au debut +","+ 3 =","3"); t("operateur au debut −","− 3 =","−3"); t("= seul","=","0"); t("= apres op","5 + =","5");
    System.out.println("== +/−");
    t("5 +/−","5 +/−","−5"); t("double +/−","5 +/− +/−","5"); t("neg + pos","5 +/− + 3 =","−2"); t("neg x neg","3 +/− × 4 +/− =","12");
    t("0 +/− (zero negatif saisi)","0 +/−","−0"); t("+/− apres =","5 + 3 = +/−","−8"); t("0,1 +/− + 0,2","0 , 1 +/− + 0 , 2 =","0,1");
    System.out.println("== Pourcentage");
    t("200+10%","2 0 0 + 1 0 % =","220"); t("200-10%","2 0 0 − 1 0 % =","180"); t("50%","5 0 %","0,5");
    t("200x5%","2 0 0 × 5 % =","10"); t("200÷50%","2 0 0 ÷ 5 0 % =","400"); t("80+25%","8 0 + 2 5 % =","100"); t("5%%","5 % %","0,0005");
    System.out.println("== Memoire");
    t("m+ m+ mr","5 m+ 3 m+ mr","8"); t("m+ m− mr","5 m+ 2 m− mr","3"); t("mc","5 m+ mc mr","0"); t("memoire survit a C","5 m+ C mr","5");
    t("mr comme operande","9 m+ C 2 × mr =","18"); t("m+ apres =","2 + 3 = m+ mr","5");
    System.out.println("== Regression V1.2 (memoire)");
    t("chiffre apres m+ = nouveau nombre","5 m+ 3","3"); t("2x3 m+ 4 =","2 × 3 m+ 4 =","8"); t("m+ puis m− puis mr","5 m+ 2 m− mr","3");
    t("mr puis chiffre remplace","5 m+ C mr 3","3"); t("m+ puis virgule","5 m+ ,","0,"); t("m+ puis ⌫ remet a 0","5 m+ ⌫","0");
    t("memoire intacte apres chiffre","7 m+ 3 m+ 1 m− mr","9");
    System.out.println("== Regression V1.2 (grands nombres)");
    t("24 neufs² en notation scientifique","9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 × 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 =","1E+48");
    t("1/3 ÷ 1e23","1 ÷ 3 ÷ 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 =","3,333333333333333E−22");
    t("negatif tres grand","9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 +/− × 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 =","−1E+48");
    System.out.println("== Retour arriere");
    t("123⌫","1 2 3 ⌫","12"); t("1⌫","1 ⌫","0"); t("1,5⌫⌫","1 , 5 ⌫ ⌫","1"); t("⌫ apres op","5 + ⌫ 3 =","8"); t("−5 ⌫","5 +/− ⌫","0");
    System.out.println("== Erreurs");
    t("1/0","1 ÷ 0 =","Erreur"); t("0/0","0 ÷ 0 =","Erreur"); t("Erreur+op ignore","1 ÷ 0 = +","Erreur"); t("Erreur puis C","1 ÷ 0 = C","0");
    t("Erreur puis chiffre","1 ÷ 0 = 5","5"); t("1/0 via operateur","1 ÷ 0 +","Erreur"); t("Erreur puis ,","1 ÷ 0 = ,","0,");
    System.out.println("== Grands nombres");
    t("10 chiffres²","9 9 9 9 9 9 9 9 9 9 × 9 9 9 9 9 9 9 9 9 9 =","99 999 999 980 000 000 001");
    i("24 neufs ×24 neufs","9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 × 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 =");
    i("22 neufs ×9999999999","9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 9 × 9 9 9 9 9 9 9 9 9 9 =");
    i("1/(1x10^23)","1 ÷ 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 =");
    i("1/3 ÷ 1e23","1 ÷ 3 ÷ 1 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 =");
    System.out.println("== Historique / ligne en attente");
    h("1+2 → 1 ligne","1 + 2 =",1); h("2+3×4 → 2 lignes","2 + 3 × 4 =",2); h("erreur → 0 ligne","1 ÷ 0 =",0); h("sans = → 0","1 + 2",0);
    CalculatorEngine c=run("5 +"); String p=c.getPending(); boolean ok="5 +".equals(p); if(ok)pass++;else fail++; System.out.println((ok?"[OK]   ":"[ECHEC]")+" ligne en attente | '"+p+"'");
    c=run("2 + 3 × 4 ="); ok=c.getHistory().equals(Arrays.asList("2 + 3 = 5","5 × 4 = 20")); if(ok)pass++;else fail++; System.out.println((ok?"[OK]   ":"[ECHEC]")+" contenu historique | "+c.getHistory());
    c=run("1 ÷ 0 ="); ok=c.isError()&&c.getPending().isEmpty(); if(ok)pass++;else fail++; System.out.println((ok?"[OK]   ":"[ECHEC]")+" etat erreur nettoye");
    System.out.println("== Regression V1.2.1 (= repete, %, +/−, virgule)");
    t("2+3= =","2 + 3 = =","8"); t("2+3= = =","2 + 3 = = =","11"); t("2x3= =","2 × 3 = =","18"); t("10÷4= =","1 0 ÷ 4 = =","0,625");
    t("5 = = (pas d'operation)","5 = =","5"); t("= repete apres C","2 + 3 = C =","0"); t("= apres nouvelle saisie","2 + 3 = 4 =","4");
    t("= repete: nouvelle op ecrase","2 + 3 = × 4 = =","80"); 
    h("= repete ajoute a l'historique","2 + 3 = =",2);
    t("50 % puis chiffre","5 0 % 5","5"); t("200 + 10 % 5 =","2 0 0 + 1 0 % 5 =","205");
    t("+/− apres = puis chiffre","5 + 3 = +/− 2","2"); t("+/− apres = affiche","5 + 3 = +/−","−8");
    t("5, puis +/− garde la virgule","5 , +/−","−5,"); t("+/− avant les chiffres","+/− 5","−5"); t("negatif decimal saisi","+/− , 5","−0,5");
    t("−0,5 + 1","+/− , 5 + 1 =","0,5"); t("5 + (−3)","5 + +/− 3 =","2"); t("5 × (−3)","5 × +/− 3 =","−15"); t("+/− garde 1,0","1 , 0 +/−","−1,0");
    System.out.println("== Robustesse: 30 000 suites de touches aleatoires");
    String[] all={"0","1","2","3","4","5","6","7","8","9",",","+","−","×","÷","=","C","⌫","+/−","%","mc","mr","m+","m−"};
    Random rr=new Random(7); int crashes=0;
    for(int k=0;k<30000;k++){
      CalculatorEngine e=new CalculatorEngine(); StringBuilder log=new StringBuilder();
      try{ for(int j=0;j<30;j++){ String key=all[rr.nextInt(all.length)]; log.append(key).append(' '); e.press(key);
            String d=e.getDisplay(); if(d==null||d.isEmpty()||d.contains("NaN")||d.contains("null")) throw new IllegalStateException("affichage invalide: "+d);
            e.getPending(); e.getHistory(); }
      }catch(Throwable ex){ crashes++; if(crashes<=3)System.out.println("  plantage: "+log+" -> "+ex); }
    }
    if(crashes==0)pass++; else fail++;
    System.out.println((crashes==0?"[OK]   ":"[ECHEC]")+" robustesse: "+crashes+" plantage(s) sur 30000 suites");
    System.out.println("== Test aleatoire (20000 expressions, reference double)");
    Random r=new Random(42); int bad=0, n=20000; String[] ops={"+","−","×","÷"};
    for(int k=0;k<n;k++){
      int terms=2+r.nextInt(4); StringBuilder keys=new StringBuilder(); double acc=0; boolean div0=false;
      for(int j=0;j<terms;j++){
        int v=r.nextInt(1000); if(j>0){ String op=ops[r.nextInt(4)]; if(op.equals("÷")&&v==0)v=1; keys.append(op).append(' '); 
          switch(op){case "+":acc+=v;break;case "−":acc-=v;break;case "×":acc*=v;break;default:acc/=v;} }
        else acc=v;
        for(char ch:Integer.toString(v).toCharArray())keys.append(ch).append(' ');
      }
      keys.append("=");
      CalculatorEngine e=run(keys.toString()); String d=e.getDisplay();
      double got; try{ got=Double.parseDouble(d.replace(" ","").replace(",",".").replace("−","-")); }catch(Exception ex){ got=Double.NaN; }
      double tol=1e-9*Math.max(1,Math.abs(acc));
      if(!(Math.abs(got-acc)<=tol)){ bad++; if(bad<=5)System.out.println("  ecart: "+keys+" ref="+acc+" obtenu="+d); }
    }
    if(bad==0)pass++; else fail++;
    System.out.println((bad==0?"[OK]   ":"[ECHEC]")+" aleatoire: "+(n-bad)+"/"+n+" corrects");
    System.out.println("\nRESUME: "+pass+" reussis, "+fail+" echecs, "+info+" cas informatifs"); if(fail>0) System.exit(1);
  }
}
