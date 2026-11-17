package utils;

/**
 * Records every step, operation count and timing of an algorithm run.
 * Steps are stored in a raw String[] that doubles in size when full.
 */
public class ExecutionTracker {
    private String[] steps;
    private int stepCount;
    private long operationCount;
    private long startTime;
    private long endTime;
    // When false, addStep() does nothing (used for fair benchmark timing).
    private boolean logging = true;

    public ExecutionTracker() {
        reset();
    }

    public void reset() {
        steps = new String[100];
        stepCount = 0;
        operationCount = 0;
        startTime = 0;
        endTime = 0;
    }

    public void setLogging(boolean logging) {
        this.logging = logging;
    }

    public boolean isLogging() {
        return logging;
    }

    public void addStep(String description) {
        if (!logging) {
            return;
        }
        if (stepCount == steps.length) {
            String[] bigger = new String[steps.length * 2];
            for (int i = 0; i < steps.length; i++) {
                bigger[i] = steps[i];
            }
            steps = bigger;
        }
        steps[stepCount] = description;
        stepCount++;
    }

    public void incrementOperations() {
        operationCount++;
    }

    public void startTimer() {
        startTime = System.nanoTime();
        endTime = 0;
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedNano() {
        if (startTime == 0) {
            return 0;
        }
        if (endTime == 0) {
            return System.nanoTime() - startTime;
        }
        return endTime - startTime;
    }

    public double getElapsedMillis() {
        return getElapsedNano() / 1000000.0;
    }

    /** Returns a trimmed copy whose length equals the number of steps. */
    public String[] getSteps() {
        String[] result = new String[stepCount];
        for (int i = 0; i < stepCount; i++) {
            result[i] = steps[i];
        }
        return result;
    }

    public int getStepCount() {
        return stepCount;
    }

    public long getOperationCount() {
        return operationCount;
    }
}
