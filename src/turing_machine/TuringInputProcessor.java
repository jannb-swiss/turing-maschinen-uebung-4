package turing_machine;

import java.util.Arrays;
import java.util.Scanner;
import static turing_machine.TuringMachine.Symbol.*;

public class TuringInputProcessor {

    private Scanner scanner;

    public TuringInputProcessor(){
        this.scanner = new Scanner(System.in);
    }

    /**
     * Liest vom Benutzer eine gültige Multiplikation im Format 'a x b' ein und gibt diese als String auf dem Band zurück.
     * @return String, der die Multiplikation im Format '0...0a0...0b0...0' auf dem Band repräsentiert.
     */
    public String getValidMultiplicationInput() {
        String userInput;
        do {
            System.out.println("Bitte geben Sie eine Multiplikation im Format 'a x b' ein:");
            userInput = scanner.nextLine();
        } while (!isValidInput(userInput));

        String[] numberAsString = userInput.split("x");
        StringBuilder tape = new StringBuilder();
        tape.append(BLANK.getSymbol());
        tape.append(getZeros(numberAsString[0]));
        tape.append(CALCULATION.getSymbol());
        tape.append(getZeros(numberAsString[1]));
        tape.append(BLANK.getSymbol());
        return tape.toString();
    }

    /**
     * Überprüft, ob die Eingabe für eine Multiplikation im richtigen Format vorliegt und gültig ist.
     * @param input Die zu überprüfende Eingabe als String.
     * @return True, wenn die Eingabe gültig ist, ansonsten false.
     */
    private boolean isValidInput(String input) {
        String[] parts = input.split("x");
        if (parts.length != 2) {
            System.out.println("Fehler: Bitte geben Sie eine Multiplikation im Format 'axb' ein.");
            return false;
        }
        try {
            int a = Integer.parseInt(parts[0].trim());
            int b = Integer.parseInt(parts[1].trim());
            if (a <= 0 || b <= 0) {
                System.out.println("Fehler: Die Multiplikation muss positive Zahlen enthalten.");
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.println("Fehler: Bitte geben Sie nur Zahlen ein.");
            return false;
        }
        return true;
    }

    /**
     * Erstellt eine Zeichenkette aus einer bestimmten Anzahl von Nullen.
     * @param number die Anzahl von Nullen als String
     * @return eine Zeichenkette aus Nullen
     * @throws NumberFormatException wenn die Eingabe keine Zahl ist oder kleiner oder gleich 0 ist.
     */
    private String getZeros(String number) {
        int n = Integer.parseInt(number);
        if (n <= 0) {
            return "";
        }
        char[] zeros = new char[n];
        Arrays.fill(zeros, '0');
        return new String(zeros);
    }

    /**
     * Fragt den Benutzer, ob das Ergebnis schrittweise angezeigt werden soll.
     * @return true, wenn der Benutzer "ja" eingegeben hat, andernfalls false.
     */
    public boolean shouldPrintStepByStep() {
        System.out.println("Möchten Sie das Ergebnis schrittweise anzeigen? (Ja/Nein)");
        String answer = scanner.nextLine().toLowerCase();
        return answer.equals("ja");
    }


}
