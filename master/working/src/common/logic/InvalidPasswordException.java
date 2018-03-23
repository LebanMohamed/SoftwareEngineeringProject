package common.logic;

/**
 * An {@link Exception} thrown if an invalid password is provided.
 */
public final class InvalidPasswordException extends Exception {

    public InvalidPasswordException() {
        super("Invalid password supplied.");
    }
}