public class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
        this.make = (make == null || make.isEmpty()) ? null : capitalize(make);
        this.model = (model == null || model.isEmpty()) ? null : capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    private String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return null;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

        public void setLicensePlate(String plate) {
        	if (!isValidPlate(plate)) {
        		 throw new IllegalArgumentException("Invalid license plate format. Must be 3 letters followed by 3 digits.");
            }
            this.licensePlate = plate;
        }
        private boolean isValidPlate(String plate) {
            if (plate == null || plate.isEmpty()) {
                return false;
            }
            
            return plate.matches("^[A-Z]{3}[0-9]{3}$");
        }

        public String getLicensePlate() {
            return licensePlate;
        }


    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    
    public String getMake() { return make; }
    public String getModel() { return model; }
    public int getYear() { return year; }
    public VehicleStatus getStatus() { return status; }

        public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
}