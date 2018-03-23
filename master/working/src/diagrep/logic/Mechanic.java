package diagrep.logic;

/**
 * A mechanic.
 */
public final class Mechanic {

    private final int id;
    private final String firstName;
    private final String surname;
    private final double hourlyWage;

    public Mechanic(int id, String firstName, String surname, double hourlyWage) {
        this.id = id;
        this.firstName = firstName;
        this.surname = surname;
        this.hourlyWage = hourlyWage;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public double getHourlyWage() {
        return hourlyWage;
    }

    /**
     * @return A concatenation of {@link #getFirstName()} and {@link #getSurname()}.
     */
    public String getFullName() {
        return String.format("%s %s", getFirstName(), getSurname());
    }
}
