package algexplorer.structures;

/**
 * One line of the bracket checker's working: the symbol, what we did, and the
 * stack afterwards.
 */
public class BracketStep {
    public final int position; // 1 is the first character of the expression
    public final char symbol;
    public final String action;
    public final String stackAfter; // the stack from bottom to top, e.g. "( ["

    public BracketStep(int position, char symbol, String action, String stackAfter) {
        this.position = position;
        this.symbol = symbol;
        this.action = action;
        this.stackAfter = stackAfter;
    }
}