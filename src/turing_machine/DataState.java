package turing_machine;

/**
 * DataState class represents a state of a Turing Machine that includes the following information:
 * The next state to transition to, represented as a string.
 * The symbol to be written on the tape during the transition, represented as a TuringMachine.Symbol object.
 * The character to be written on the tape during the transition, represented as a char.
 */
public class DataState {
    private final String secondState;
    private final TuringMachine.Symbol symbol; //Direction
    private final char write;

    public DataState(String secondState, TuringMachine.Symbol symbol, char write) {
        this.secondState = secondState;
        this.symbol = symbol;
        this.write = write;
    }

    public String getSecondState() {
        return secondState;
    }

    public TuringMachine.Symbol getSymbol() {
        return symbol;
    }

    public char getWrite() {
        return write;
    }
}
