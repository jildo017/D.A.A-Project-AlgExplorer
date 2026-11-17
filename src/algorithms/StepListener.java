package algorithms;

/**
 * Notified after every step so the UI can animate it.
 *
 * Sorting algorithms:
 *   currentArray      the array right now (copy it if you keep it)
 *   highlightIndex1   main active index (orange) or -1
 *   highlightIndex2   second active index (red) or -1
 *   sortedBoundary    >= 0  : indices [0, sortedBoundary) are sorted (green)
 *                     -1    : nothing marked as sorted
 *                     <= -2 : indices [-sortedBoundary - 2, length) are sorted (green)
 *
 * Recursive algorithms use the same method with a different meaning:
 *   Factorial / Fibonacci : currentArray = the n value of each active call (bottom to top),
 *                           highlightIndex1 = top frame, highlightIndex2 = 1 if returning else 0
 *   Tower of Hanoi        : currentArray[i] = peg (0=A, 1=B, 2=C) of disk i+1,
 *                           highlightIndex1 = disk that just moved, highlightIndex2 = its peg
 *   Recursive Binary Search: currentArray = sorted array, highlightIndex1 = mid,
 *                           highlightIndex2 = high, sortedBoundary = low
 */
public interface StepListener {
    void onStep(int[] currentArray, int highlightIndex1, int highlightIndex2, int sortedBoundary, String statusMessage);
}
