import java.util.Scanner;
public class Main {
    public static void main(String[] args) {

        Scanner meinScanner = new Scanner(System.in);
        String Antwort ="JA";
        while (!Antwort.equals("Nein")) {
            System.out.println("Gib deine Aufgabe ein (Beispiel: 2*(3+5)+4^2%/(2*2)): ");
            String rechnung = meinScanner.next();
            rechnung = "(1,*,1),*," +formateTaskInput(rechnung);
            rechnung = rechnung.replace(")", ">").replace("(", "<");
            System.out.println("Aufgabe: " + rechnung.replace(">", ")").replace("<", "("));
            rechnung = rechnungKlammernAufloesen(rechnung);
            String[] punktE = punktVorStrich(rechnung);
            double ergebnis = rechnungOhneKlammern(punktE[punktE.length - 1]);
            System.out.println("Klammern auflösen: " + rechnung);
          for (String zwischenE : punktE) {
                System.out.println("Punktrechnung: " + zwischenE);
            }
            System.out.println("Ergebnis: " + ergebnis);
            System.out.println("Willst du weiterrechnen (Nein, oder Ja), case sensitive: ");
            Antwort = meinScanner.next();
        }
        meinScanner.close();
    }
     public static String formateTaskInput(String aufgabe){
         String vorherigesElement="ß";
         String[] symbolA = stringToStringArray(aufgabe);
          for (int i =0; i<symbolA.length; i++) {
              String symbol = symbolA[i];
              if(!vorherigesElement.equals("(")&&!symbol.equals(")")&&!Character.isDigit(symbol.toCharArray()[0])&&Character.isDigit(vorherigesElement.toCharArray()[0])
                      ||!vorherigesElement.equals("(")&&!symbol.equals(")")&&Character.isDigit(symbol.toCharArray()[0])&&!Character.isDigit(vorherigesElement.toCharArray()[0])
              ){
                      symbolA = makeSpaceInArray(symbolA, i, 1);
                      symbolA[i] = ",";
                      i++;
              } else if (vorherigesElement.equals(")")) {
                  symbolA = makeSpaceInArray(symbolA, i, 1);
                  symbolA[i] = ",";
                  i++;}else if (symbol.equals("(")) {
                  symbolA = makeSpaceInArray(symbolA, i, 1);
                  symbolA[i] = ",";
                  i++;
              }
              vorherigesElement = symbol;
          }
         symbolA= deleteItemsInArray(symbolA,0,1);
          System.out.println(String.join("",symbolA));
          return String.join("",symbolA);
      }
    public static void showArray(String[] array) {
        for (String inhalt : array) {
            System.out.println(inhalt);
        }
    }

    public static String[] stringToStringArray(String input){
        char[] cA= input.toCharArray();
        String[] stringA = new String[input.length()];
        for (int i=0; i<cA.length; i++){
            stringA[i]= Character.toString(cA[i]);
        }
        return stringA;
    }

    private static String rechnungKlammernAufloesen(String aufgabe){
        String[] aufgabeMKlammern = aufgabe.split("<");
        for (int i=0;i<aufgabeMKlammern.length; i++){
            String splitedString = aufgabeMKlammern[i];
            if(splitedString!=null&&!splitedString.isEmpty()){
                String[] klammeSeperatedString= splitedString.split(">");
                if(klammeSeperatedString.length>1&&!klammeSeperatedString[1].isEmpty()){
                    aufgabeMKlammern=   makeSpaceInArray(aufgabeMKlammern,i+1,1 );
                    aufgabeMKlammern[i+1]= klammeSeperatedString[1];
                }
                String[] punktVorStrich = punktVorStrich(klammeSeperatedString[0]);
                String punktVorStrichEnd = punktVorStrich.length>0 ? punktVorStrich[punktVorStrich.length-1]: "0,+,0";
                aufgabeMKlammern[i]= Double.toString(rechnungOhneKlammern(punktVorStrichEnd));
                i+=1;
            }
        }
        return String.join("",aufgabeMKlammern);
    }
    private static String[] punktVorStrich(String aufgabe){
        String[] operationA = aufgabe.split(",");
        String[] aufgabenVerlauf= new String[1000];
        aufgabenVerlauf[0]= aufgabe;
        int anzahlOperationen= 0;
        while(String.join(",",operationA).contains("/")||String.join(",",operationA).contains("*")|| String.join(",",operationA).contains("^")) {
            for (int i = 0; i < operationA.length; i++) {
                String rechenOperator = operationA[i];
                if(!String.join("",operationA).contains("^")){
                    if (rechenOperator.equals("/") || rechenOperator.equals("*")) {
                        try {
                            double z1 = Double.parseDouble(operationA[i - 1]);
                            double z2 = Double.parseDouble(operationA[i + 1]);
                            double ergebnis = runRightOperation(z1, z2, rechenOperator);
                            operationA[i - 1] = Double.toString(ergebnis);
                            operationA = deleteItemsInArray(operationA, i, 2);
                        } catch (Exception e) {
                            System.out.println("Fehler in der Verarbeitung der Rechnung, Ergebnis möglicherweise falsch");
                            break;
                        }
                    }
                }else {
                    if (rechenOperator.equals("^")) {
                        try {
                            double z1 = Double.parseDouble(operationA[i - 1]);
                            double z2 = Double.parseDouble(operationA[i + 1]);
                            double ergebnis = runRightOperation(z1, z2, rechenOperator);
                            operationA[i - 1] = Double.toString(ergebnis);
                            operationA = deleteItemsInArray(operationA, i, 2);
                        } catch (Exception e) {
                            System.out.println("Fehler in der Verarbeitung der Rechnung, Ergebnis möglicherweise falsch");
                            break;
                        }
                    }
                }
            }
            aufgabenVerlauf[anzahlOperationen>=aufgabenVerlauf.length ? aufgabenVerlauf.length-1 : anzahlOperationen]= String.join(",",operationA);
            anzahlOperationen++;
        }
    aufgabenVerlauf= deleteItemsInArray(aufgabenVerlauf,anzahlOperationen==0 ? 1 : anzahlOperationen, anzahlOperationen==0 ? aufgabenVerlauf.length-1 : aufgabenVerlauf.length-anzahlOperationen);
        return aufgabenVerlauf;
    }
    private static double rechnungOhneKlammern(String aufgabe){
        double vorherigesErgebnis= 0;
        String[] operationA = aufgabe.split(",");
        for (int i = -1; i < operationA.length; i +=2) {
            double zahl1= vorherigesErgebnis, zahl2=0;
            try {
                zahl2 = Double.parseDouble(operationA[i + 1]);
            } catch (Exception e){
                System.out.println("Fehler in der Verarbeitung der Rechnung, Ergebnis möglicherweise falsch");
                break;
            }
            String rechenOperator =i<0 ? "+" :operationA[i];
            vorherigesErgebnis= runRightOperation(zahl1, zahl2, rechenOperator);
        }
        return vorherigesErgebnis;
    }
    private static String[] deleteItemsInArray(String[] array, int index, int count){
        String[] newArray = new String[(array.length-count)];
        for (int i=0; i< array.length; i++){
            if(index>i){
                newArray[i]= array[i];
            } else if(index+count<=i){
                newArray[i-count]= array[i];
            }
        }
        return  newArray;
    }
    private static String[] makeSpaceInArray(String[] array, int index, int count){
        String[] newArray = new String[(array.length+count)];
        for (int i=0; i< newArray.length; i++){
            if(index>i){
                newArray[i]= array[i];
            } else if(index+count<=i){
                newArray[i]= array[i-count];
            }
        }
        return  newArray;
    }
    private  static double runRightOperation(double z1, double z2, String operator){
        double Ergebnis=0;
        switch (operator){
            case "+": return z1+ z2;
            case "*": return z1* z2;
            case "/": return z1/ z2;
            case "-": return z1- z2;
            case "^": return Math.pow(z1,z2);
            case "%": return z1%z2;
            default:
                System.out.println("Fehler Rechenoperator nicht erkannt");
                break;
        }
        return Ergebnis;
    }
}