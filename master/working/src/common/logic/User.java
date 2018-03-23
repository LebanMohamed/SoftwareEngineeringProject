package common.logic;

/**
 * A system user.
 */
public final class User {

    /**
     * The user id, this is unique to each user and should not be changeable.
     */
    private final int id;
    private String firstName;
    private String surname;
    private String password;
    private boolean admin;

    /**
     * Constructs a new User with the given details.
     */
    public User(int id, String firstName, String surname, String password, boolean admin) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.password = password;
        this.admin = admin;
    }

    public int getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns whether or not this user is an administrator.
     */
    public boolean isAdmin() {
        return this.admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getFullName() {
        return firstName + " " + surname;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof User) {
            User user = (User)obj;
            return user.id == id && user.firstName.equals(firstName) && user.surname.equals(surname)
                    && user.password.equals(password) && user.admin == admin;
        }
        return false;
    }

    @Override
    public String toString() {
        return "User(" + id + "," + firstName + "," + surname + "," + password + "," + admin + ")";
    }
}