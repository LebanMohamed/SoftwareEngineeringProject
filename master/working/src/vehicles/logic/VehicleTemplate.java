package vehicles.logic;

public class VehicleTemplate {

    public enum VehicleType {
        CAR,
        VAN,
        TRUCK
    }

    protected int id;
    private String Model;
    private String Make;
    private double EngineSize;
    private String FuelType;
    private final VehicleType type;

    public VehicleTemplate(int id, String Model, String Make, double EngineSize,
            String FuelType, VehicleType vtype) {
        this.id = id;
        this.Model = Model;
        this.Make = Make;
        this.EngineSize = EngineSize;
        this.FuelType = FuelType;
        this.type = vtype;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getModel() {
        return this.Model;
    }

    public void setModel(String Model) {
        this.Model = Model;
    }

    public String getMake() {
        return this.Make;
    }

    public void setMake(String Make) {
        this.Make = Make;
    }

    public double getEngineSize() {
        return this.EngineSize;
    }

    public void setEngineSize(double EngineSize) {
        this.EngineSize = EngineSize;
    }

    public String getFuelType() {
        return this.FuelType;
    }

    public void setFuelType(String FuelType) {
        this.FuelType = FuelType;
    }

    public VehicleType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return id + " " + Make + " " + Model + " " + EngineSize + " " + FuelType + " " + type;
    }

}
