package turing_machine;

import static turing_machine.TuringMachine.Symbol.*;

import turing_machine.TuringMachine.Symbol;

/**
 * Eine Übergangsfunktion zwischen zwei Zuständen eines Turing-Maschine-Programms.
 * Der Übergangsfunktion besteht aus dem aktuellen Zustand, dem gelesenen Zeichen, dem neuen Zustand,
 * dem zu schreibenden Zeichen und der Bewegungsrichtung des Lesekopfs.
 */
public class Transition {

    private Symbol direction;
    private String firstState;
    private String secondState;
    private char write;
    private char read;


    /**
     * Erstellt eine Übergangsfunktion mit Standardwerten.
     * Der erste Zustand ist "q0", das gelesene Zeichen ist '0', der zweite Zustand ist "q1",
     * das zu schreibende Zeichen ist '1' und die Bewegungsrichtung ist "RIGHT".
     */
    public Transition() {
        this.firstState = "q0";
        this.read = '0';
        this.secondState = "q1";
        this.write = '1';
        this.direction = RIGHT;
    }

    /**
     * Erstellt eine Übergangsfunktion mit angegebenen Werten.
     *
     * @param firstState  der erste Zustand der Übergangsfunktion
     * @param read        das gelesene Zeichen der Übergangsfunktion
     * @param secondState der zweite Zustand der Übergangsfunktion
     * @param write       das zu schreibende Zeichen der Übergangsfunktion
     * @param direction      die Bewegungsrichtung des Lesekopfs der Übergangsfunktion
     */
    public Transition(String firstState, char read, String secondState, char write, Symbol direction) {
        this.firstState = firstState;
        this.secondState = secondState;
        this.read = read;
        this.write = write;
        this.direction = direction;
    }

    /**
     * Gibt den neuen Zustand und die Aktion (Zeichen schreiben und Lesekopf bewegen) zurück,
     * die von dieser Übergangsfunktion ausgeführt werden soll.
     *
     * @return der neue Zustand und die Aktion, die von dieser Übergangsfunktion ausgeführt werden soll
     */
    public DataState apply() {
        return new DataState(secondState, direction, write);
    }

    /**
     * Gibt eine String-Repräsentation dieser Übergangsfunktion zurück.
     * Die Repräsentation hat das Format "(firstState ; read) = (secondState ; write ; symbol)".
     *
     * @return eine String-Repräsentation dieser Übergangsfunktion
     */
    @Override
    public String toString() {
        return "(" + firstState + " ; " + read + ") = (" + secondState + " ; " + write + " ; " + direction.getSymbol() + ")";
    }

    public String getFirstState() {
        return firstState;
    }

    public char getRead() {
        return read;
    }

}
