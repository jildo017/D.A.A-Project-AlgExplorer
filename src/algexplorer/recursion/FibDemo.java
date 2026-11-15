package algexplorer.recursion;

import algexplorer.core.Result;

public class FibDemo {
    public static void main(String[] args) {
        int[] tests = { 5, 10, 20, 30 };
        for (int n : tests) {
            Result a = Fibonacci.runNaive(n);
            Result b = Fibonacci.runMemo(n);
            Result c = Fibonacci.runBottomUp(n);
            System.out.println("n=" + n + "  value=" + a.value);
            System.out.println("   naive    calls=" + a.operations + "  " + a.nanos + " ns");
            System.out.println("   memo     calls=" + b.operations + "  " + b.nanos + " ns");
            System.out.println("   bottomUp loops=" + c.operations + "  " + c.nanos + " ns");
        }
    }
}