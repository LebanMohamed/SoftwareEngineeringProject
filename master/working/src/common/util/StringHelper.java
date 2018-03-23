package common.util;

/**
 * A utility class for {@link String} objects.
 */
public final class StringHelper {

    /**
     * Source: https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java/18591205#18591205
     * @return If argument contains digit(s).
     */
    public static boolean containsDigit(String s) {
        if (s != null && !s.isEmpty())
            for (char c : s.toCharArray())
                if (Character.isDigit(c))
                    return true;
        return false;
    }

    /**
     * A name is considered valid if it satisfies the following properties:
     * 1. It is non-empty.
     * 2. It consists only of letters, as defined by {@link Character#isLetter(char)}, spaces and hyphens.
     * 3. It must start with and end with a letter (see definition above).
     * 4. It has no consecutive spaces or hyphens.
     * 5. A hyphen can only be placed between two letters.
     * @return If the provided name is valid.
     */
    public static boolean validName(String name) {
        char[] letters = name.toCharArray();

        // Check for conditions (3) and (4).
        boolean containsBadConsecutives = name.contains("--") || name.contains("  ");
        boolean invalidStartOrEnd = name.startsWith(" ") || name.endsWith(" ")
                || name.startsWith("-") | name.endsWith("-");

        // Iterate over each character, check for conditions (2) and (5).
        for (int i = 0; i < letters.length; i++) {
            char c = letters[i];
            boolean hasNext = i + 1 < letters.length;
            boolean hasPrevious = i > 0;

            boolean invalidLetter = !Character.isLetter(c) && c != ' ';
            boolean validHyphen = c == '-' && hasNext && letters[i+1] != ' '
                    && Character.isLetter(letters[i+1]) && hasPrevious && Character.isLetter(letters[i-1]);

            if (invalidLetter && !validHyphen) {
                return false;
            }
        }
        return !name.isEmpty() && !containsBadConsecutives && !invalidStartOrEnd;
    }
}
