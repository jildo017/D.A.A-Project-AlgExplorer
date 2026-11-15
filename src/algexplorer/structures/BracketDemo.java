package algexplorer.structures;

public class BracketDemo {
    private static void show(String expression, boolean showSteps) {
        System.out.println("Expression: \"" + expression + "\"");
        try {
            BracketResult r = BracketChecker.check(expression);
            for (int i = 0; showSteps && i < r.steps.length; i++) {
                BracketStep s = r.steps[i];
                System.out.println("  " + s.position + "  " + s.symbol + "   " + s.action
                        + "   stack: " + s.stackAfter);
            }
            System.out.println("  Result: " + (r.balanced ? "BALANCED" : "NOT BALANCED"));
            System.out.println("  " + r.message);
            System.out.println("  Operations = " + r.operations);
        } catch (IllegalArgumentException e) {
            System.out.println("  Rejected: " + e.getMessage());
        }
        System.out.println();
    }

    public static void main(String[] args) {
        show("{ ( [ ] ) }", true);
        show("( [ ) ]", true);
        show("(((", true);
        show("())", true);
        show("a + (b * [c - d]) / {e}", true);
        show("abc", true);
        show("   ", true);
        show("(((((((((((((((((((((", false); // 21 opening brackets
    }
}