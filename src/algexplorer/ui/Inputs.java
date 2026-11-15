package algexplorer.ui;

/** Small helper that reads and checks numbers typed by the user. */
final class Inputs {
    private Inputs() {
    }

    /**
     * Turns text into a whole number from min to max, or throws a friendly message.
     */
    static int readInt(String text, String name, int min, int max) {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) {
            throw new IllegalArgumentException("Please type a number for " + name + ".");
        }
        int value;
        try {
            value = Integer.parseInt(t);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException(name + ": \"" + t + "\" is not a whole number.");
        }
        if (value < min || value > max) {
            throw new IllegalArgumentException(name + " must be from " + min + " to " + max + ".");
        }
        return value;
    }
}