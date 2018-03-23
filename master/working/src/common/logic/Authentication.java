package common.logic;

import common.Database;

import java.sql.SQLException;

public final class Authentication {

    /**
     * Performs a login, by checking the given password against the target {@link User}'s password.
     */
    public static User attemptLogin(int id, String password) throws InvalidPasswordException, SQLException {
        if (!Database.getInstance().userExists(id))
            throw new InvalidPasswordException(); // XXX we indicate incorrect password if such user does not exist
        User user = Database.getInstance().getUser(id);

        if (user.getPassword().equals(password))
            return user;
        else
            throw new InvalidPasswordException();
    }
}
