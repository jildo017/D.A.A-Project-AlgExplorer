package algexplorer.structures;

import algexplorer.core.OpCounter;
import algexplorer.core.StopWatch;

/**
 * Checks if the brackets ( ) [ ] { } in an expression are balanced, using our
 * own ArrayStack.
 */
public class BracketChecker {
    public static final int MAX_LENGTH = 60;
    public static final int MAX_DEPTH = ArrayStack.MAX_CAPACITY;

    private static boolean isOpening(char c) {
        return c == '(' || c == '[' || c == '{';
    }

    private static boolean isClosing(char c) {
        return c == ')' || c == ']' || c == '}';
    }

    private static boolean matches(char open, char close) {
        return (open == '(' && close == ')')
                || (open == '[' && close == ']')
                || (open == '{' && close == '}');
    }

    /** The stack from bottom to top as text, e.g. "( [". Time: O(n). */
    private static String show(ArrayStack stack) {
        if (stack.isEmpty()) {
            return "(empty)";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < stack.size(); i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append((char) stack.get(i));
        }
        return sb.toString();
    }

    /**
     * Checks the expression. Other characters (letters, digits, spaces) are
     * ignored. Time: O(n).
     */
    public static BracketResult check(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Please type an expression to check.");
        }
        if (expression.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("The expression is too long (maximum "
                    + MAX_LENGTH + " characters).");
        }

        OpCounter counter = new OpCounter();
        StopWatch watch = new StopWatch();
        ArrayStack stack = new ArrayStack(MAX_DEPTH);
        BracketStep[] all = new BracketStep[expression.length()];
        int count = 0;
        boolean ok = true;
        String message = "";

        watch.start();
        for (int i = 0; i < expression.length() && ok; i++) {
            char c = expression.charAt(i);
            counter.add(); // one step for looking at a character
            if (isOpening(c)) {
                try {
                    stack.push(c);
                    counter.add();
                    all[count++] = new BracketStep(i + 1, c,
                            "Push '" + c + "' (opening bracket)", show(stack));
                } catch (IllegalStateException e) {
                    ok = false;
                    message = "Too many brackets open at once: the stack is full (maximum "
                            + MAX_DEPTH + ").";
                    all[count++] = new BracketStep(i + 1, c, "Push failed: stack overflow", show(stack));
                }
            } else if (isClosing(c)) {
                if (stack.isEmpty()) {
                    ok = false;
                    message = "Not balanced: the closing '" + c + "' at position " + (i + 1)
                            + " has no opening bracket before it.";
                    all[count++] = new BracketStep(i + 1, c,
                            "Stack is empty, nothing to match", show(stack));
                } else {
                    char open = (char) stack.pop();
                    counter.add();
                    if (matches(open, c)) {
                        all[count++] = new BracketStep(i + 1, c,
                                "Pop '" + open + "' (it matches '" + c + "')", show(stack));
                    } else {
                        ok = false;
                        message = "Not balanced: the closing '" + c + "' at position " + (i + 1)
                                + " does not match the opening '" + open + "' on top of the stack.";
                        all[count++] = new BracketStep(i + 1, c,
                                "Pop '" + open + "' but it does not match '" + c + "'", show(stack));
                    }
                }
            }
        }

        if (ok && !stack.isEmpty()) {
            ok = false;
            message = "Not balanced: " + stack.size() + " opening bracket(s) were never closed. "
                    + "Left on the stack (bottom to top): " + show(stack);
        }
        if (ok) {
            message = count == 0
                    ? "Balanced (the expression contains no brackets)."
                    : "Balanced: every opening bracket has a matching closing bracket, in the right order.";
        }
        watch.stop();

        BracketStep[] steps = new BracketStep[count]; // keep only the steps we used
        for (int i = 0; i < count; i++) {
            steps[i] = all[i];
        }
        return new BracketResult(ok, message, steps, counter.get(), watch.getNanos());
    }
}