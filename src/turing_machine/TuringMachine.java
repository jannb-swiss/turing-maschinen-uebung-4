package turing_machine;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static turing_machine.TuringMachine.Symbol.*;

import static java.lang.Thread.sleep;

/**
 * Eine Turing-Maschine, die eine Eingabe liest und basierend auf einer vorher
 * definierten Liste von Übergangsfunktionen eine Ausgabe produziert.
 */
public class TuringMachine {

    private String state = "q0";
    private String tape;
    private long counter = 0;
    private int pointerLocation = 1;
    private boolean printSteps;
    private TuringInputProcessor turingInputProcessor = new TuringInputProcessor();
    private List<Transition> transitionList;

    /**
     * Erstellt eine neue Turing-Maschine, die die Eingabe vom Benutzer
     * einliest, die Übergangsfunktionen aus einer Datei lädt und sie dann
     * ausführt.
     */
    public TuringMachine(String txtPath) {
        Path path = Path.of(txtPath);
        this.tape = turingInputProcessor.getValidMultiplicationInput();
        this.printSteps = turingInputProcessor.shouldPrintStepByStep();
        transitionList = new ArrayList<>();
        parseByteCodeAndAddTransitions(path);
        run();
    }


    /**
     * Führt die Turing-Maschine aus, indem sie die Übergangsfunktionen
     * der Reihe nach auf die Eingabe anwendet, bis es keine passende
     * Übergangsfunktion mehr gibt. Dabei wird auch die Ausgabe produziert.
     */
    private void run() {
        Transition transition = new Transition();

        if (printSteps) {
            while ((transition = findApplicableTransition()) != null) {
                counter++;
                DataState dataState = transition.apply();
                updateTapeAtPosition(dataState.getWrite(), pointerLocation);
                if (dataState.getSymbol() == LEFT) {
                    pointerLocation--;
                } else if (dataState.getSymbol() == RIGHT) {
                    pointerLocation++;
                }
                expandTapeIfNeeded();
                state = dataState.getSecondState();

                printSteps(transition);
            }

            System.out.println(tape);
        } else {
            while ((transition = findApplicableTransition()) != null) {
                counter++;
                DataState dataState = transition.apply();
                updateTapeAtPosition(dataState.getWrite(), pointerLocation);
                if (dataState.getSymbol() == LEFT) {
                    pointerLocation--;
                } else if (dataState.getSymbol() == RIGHT) {
                    pointerLocation++;
                }
                expandTapeIfNeeded();
                state = dataState.getSecondState();
            }
        }

        int result = calculateOutput();
        System.out.println("");
        System.out.println("Resultat: " + result);
        System.out.println("");

        this.pointerLocation = 1;

    }

    /**
     * Wandelt den Inhalt des Tapes in eine Ausgabe um, die vom Benutzer
     * interpretiert werden kann.
     *
     * @return die Ausgabe der Turing-Maschine
     */
    private int calculateOutput() {
        String[] fragments = tape.split(BLANK.getSymbolAsString());
        int lastIndex = fragments.length - 1;

        if (tape.contains(BLANK.getSymbolAsString().repeat(2))) {
            return 0;
        } else {
            return fragments[lastIndex].length();
        }
    }

    /**
     * Gibt die Details einer Transition und den aktuellen Zustand der Turing-Maschine aus.
     *
     * @param transition die Transition, die ausgeführt wurde
     */
    private void printSteps(Transition transition) {
        System.out.println("Step Nr: " + counter);
        System.out.println(transition);
        System.out.println(tape.replace(" ", ""));
        printMachineState();
        try {
            sleep(100);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ;
    }

    /**
     * Gibt den aktuellen Zustand der Turing-Maschine aus, indem ein Zeiger auf das Tape
     * gedruckt wird, der die Position der Maschine anzeigt.
     */
    private void printMachineState() {
        StringBuilder tapeBuilder = new StringBuilder();
        for (int i = 0; i < tape.length(); i++) {
            if (i == pointerLocation) {
                tapeBuilder.append("^");
            } else {
                tapeBuilder.append(" ");
            }
        }
        System.out.println(tapeBuilder.toString());
    }

    /**
     * Ersetzt das Zeichen an der angegebenen Position auf dem Band durch das angegebene neue Zeichen.
     *
     * @param newCharacter    das neue Zeichen, das an der angegebenen Position auf dem Band gesetzt werden soll
     * @param pointerLocation die Position auf dem Band, an der das Zeichen ersetzt werden soll
     */
    public void updateTapeAtPosition(char newCharacter, int pointerLocation) {
        tape = tape.substring(0, pointerLocation) + newCharacter + tape.substring(pointerLocation + 1);
    }


    /**
     * Sucht nach der nächsten anwendbaren Transition basierend auf der aktuellen Konfiguration
     * der Turing-Maschine.
     *
     * @return die nächste anwendbare Transition oder null, wenn keine gefunden wurde
     */
    private Transition findApplicableTransition() {
        return transitionList.stream()
                .filter(t -> t.getFirstState().equals(state) && t.getRead() == getActualCharacter())
                .findFirst()
                .orElse(null);
    }

    /**
     * Gibt das aktuelle Zeichen auf dem Band zurück, das vom Lese-/Schreibkopf gelesen wird.
     *
     * @return das aktuelle Zeichen auf dem Band
     */
    public char getActualCharacter() {
        return tape.charAt(pointerLocation);
    }

    /**
     * Konvertiert den Bytecode in eine Liste von Übergängen und fügt sie der Übergangsliste hinzu.
     *
     * @param codePath der Pfad zur Bytecode-Datei
     */
    public void parseByteCodeAndAddTransitions(Path codePath) {
        String[] transitions = convertByteCodeToTransitions(codePath);

        for (String transition : transitions) {
            String[] fragments = transition.split(SPLIT.getSymbolAsString());
            String fromState = "q" + (fragments[0].length() - 1);
            String toState = "q" + (fragments[2].length() - 1);
            char readChar = getSymbolTypeFromDigit(fragments[1].length());
            char writeChar = getSymbolTypeFromDigit(fragments[3].length());
            Symbol direction = fragments[4].length() == 1 ? LEFT : RIGHT;
            transitionList.add(new Transition(fromState, readChar, toState, writeChar, direction));
        }
    }


    /**
     * Erweitert das Band um ein zusätzliches Blank-Symbol links oder rechts vom aktuellen Positionierungszeiger,
     * falls dieser am Rand des Bands steht.
     */
    private void expandTapeIfNeeded() {
        if (pointerLocation >= tape.length()) {
            tape += BLANK.getSymbol();
        } else if (pointerLocation < 0) {
            tape = BLANK.getSymbol() + tape;
            pointerLocation++;
        }
    }

    /**
     * Wandelt eine Ziffer in einen entsprechenden Charakter um, der den Typ des Symbols darstellt.
     *
     * @param digit Die Ziffer, die übersetzt werden soll.
     * @return Der entsprechende Charakter, der den Typ des Symbols darstellt.
     */
    private char getSymbolTypeFromDigit(int digit) {
        switch (digit) {
            case 1:
                return NUMBER.getSymbol();
            case 2:
                return SPLIT.getSymbol();
            case 3:
                return BLANK.getSymbol();
            case 4:
                return CALCULATION.getSymbol();
            case 5:
                return REPLACE_ONE.getSymbol();
            case 6:
                return REPLACE_ZERO.getSymbol();
            default:
                throw new IllegalArgumentException("Ungültige Ziffer: " + digit);
        }
    }

    /**
     * Liest den Inhalt einer Bytecode-Datei ein, teilt sie in einzelne Übergänge auf und gibt diese als Array von Strings zurück.
     *
     * @param codePath der Pfad zur Bytecode-Datei, die eingelesen werden soll
     * @return ein Array von Strings, das die einzelnen Übergänge aus der Bytecode-Datei enthält
     * @throws RuntimeException wenn ein Fehler beim Lesen der Datei auftritt
     */
    public String[] convertByteCodeToTransitions(Path codePath) {
        try {
            StringBuilder fileContents = readFileContents(codePath);
            String withoutLeadingOne = removeLeadingCharacter(fileContents);
            String[] transitionsAsString = splitTransitions(withoutLeadingOne);
            return removeLineBreaks(transitionsAsString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Liest den Inhalt einer Datei ein und gibt eine StringBuilder-Instanz zurück, die den Inhalt enthält.
     *
     * @param codePath der Pfad zur Datei, deren Inhalt gelesen werden soll
     * @return ein StringBuilder-Objekt, das den Inhalt der Datei enthält
     * @throws IOException wenn ein Fehler beim Lesen der Datei auftritt
     */
    public StringBuilder readFileContents(Path codePath) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(codePath)) {
            String line = "";
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
        }
        return stringBuilder;
    }

    /**
     * Entfernt das erste Zeichen aus einem StringBuilder-Objekt und gibt die verbleibende Zeichenfolge zurück.
     *
     * @param stringBuilder das StringBuilder-Objekt, dessen erstes Zeichen entfernt werden soll
     * @return eine Zeichenfolge, die den Inhalt des StringBuilder-Objekts ohne das erste Zeichen enthält
     */
    public String removeLeadingCharacter(StringBuilder stringBuilder) {
        return stringBuilder.substring(1);
    }

    /**
     * Trennt eine Bytecode-Zeichenfolge anhand des definierten Trennzeichens in einzelne Übergänge auf und gibt diese als Array von Strings zurück.
     *
     * @param transitionsAsString die Bytecode-Zeichenfolge, die in einzelne Übergänge aufgeteilt werden soll
     * @return ein Array von Strings, das die einzelnen Übergänge aus der Bytecode-Zeichenfolge enthält
     */
    public String[] splitTransitions(String transitionsAsString) {
        return transitionsAsString.split(SPLIT.getSymbolAsString().repeat(2));
    }

    /**
     * Entfernt alle Zeilenumbrüche aus einem Array von Strings und gibt das bereinigte Array zurück.
     *
     * @param transitionsAsString das Array von Strings, aus dem die Zeilenumbrüche entfernt werden sollen
     * @return ein bereinigtes Array von Strings, das alle Zeilenumbrüche entfernt hat
     */
    public String[] removeLineBreaks(String[] transitionsAsString) {
        String[] betterTransitionString = new String[transitionsAsString.length - 1];
        for (int i = 0; i < betterTransitionString.length; i++) {
            betterTransitionString[i] = transitionsAsString[i].replace("\n", "");
        }
        return betterTransitionString;
    }

    public enum Symbol {
        SPLIT('1'),
        NUMBER('0'),
        REPLACE_ZERO('X'),
        REPLACE_ONE('Y'),
        BLANK('_'),
        CALCULATION('C'),
        LEFT('L'),
        RIGHT('R');

        char symbol;

        Symbol(char symbol) {
            this.symbol = symbol;
        }

        char getSymbol() {
            return symbol;
        }

        String getSymbolAsString() {
            return String.valueOf(symbol);
        }
    }
}
