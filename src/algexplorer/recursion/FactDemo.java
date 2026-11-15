package algexplorer.recursion;

import algexplorer.core.Result;

public class FactDemo {
    public static void main(String[] args) {
        int[] tests = { 0, 5, 10, 20 };
        for (int n : tests) {
            Result r = Factorial.runRecursive(n);
            Result i = Factorial.runIterative(n);
            System.out.println(n + "! = " + r.value
                    + "   recursive calls=" + r.operations
                    + "   iterative loops=" + i.operations);
        }

        int[] bad = { -1, 21 };
        for (int n : bad) {
            try {
                Factorial.runRecursive(n);
            } catch (IllegalArgumentException e) {
                System.out.println("n=" + n + " rejected: " + e.getMessage());
            }
        }

        try {
            Fibonacci.runNaive(31);
        } catch (IllegalArgumentException e) {
            System.out.println("fib naive n=31 rejected: " + e.getMessage());
        }
    }
}