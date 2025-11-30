import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;


public class RentalSystem {

	    private static RentalSystem instance;  

	    private List<Vehicle> vehicles;
	    private List<Customer> customers;
	    private RentalHistory rentalHistory;

	    private RentalSystem() {
	        vehicles = new ArrayList<>();
	        customers = new ArrayList<>();
	        rentalHistory = new RentalHistory();
	        loadData();
	    }

	    public static RentalSystem getInstance() {
	        if (instance == null) {
	            instance = new RentalSystem();
	        }
	        return instance;
	    }
	    
	    private void loadData() {
	 	        try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                String[] parts = line.split(",");
	                if (parts.length >= 5) {
	                    String plate = parts[0];
	                    String make = parts[1];
	                    String model = parts[2];
	                    int year = Integer.parseInt(parts[3]);
	                    Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[4]);

	                    Vehicle v = new Vehicle(make, model, year);
	                    v.setLicensePlate(plate);
	                    v.setStatus(status);
	                    vehicles.add(v);
	                }
	            }
	 	       } catch (Exception e) {
	 	            System.out.println("No vehicles loaded: " + e.getMessage());
	 	        }

	 	        try (BufferedReader reader = new BufferedReader(new FileReader("customers.txt"))) {
	 	        String line;
	 	        while ((line = reader.readLine()) != null) {
	 	                String[] parts = line.split(",");
	 	                if (parts.length >= 2) {
	 	                    int id = Integer.parseInt(parts[0]);
	 	                    String name = parts[1];
	 	                    customers.add(new Customer(id, name));
	 	                }
	 	            }
	 	       } catch (Exception e) {
	 	            System.out.println("No customers loaded: " + e.getMessage());
	 	        }

	 	        // Load rental records
	 	        try (BufferedReader reader = new BufferedReader(new FileReader("rental_records.txt"))) {
	 	            String line;
	 	            while ((line = reader.readLine()) != null) {
	 	                String[] parts = line.split(",");
	 	                if (parts.length >= 5) {
	 	                    String type = parts[0];
	 	                    String plate = parts[1];
	 	                    String customerName = parts[2];
	 	                    LocalDate date = LocalDate.parse(parts[3]);
	 	                    double amount = Double.parseDouble(parts[4]);

	 	                    Vehicle v = findVehicleByPlate(plate);
	 	                    Customer c = customers.stream()
	 	                                          .filter(cust -> cust.getCustomerName().equals(customerName))
	 	                                          .findFirst()
	 	                                          .orElse(null);

	 	                    if (v != null && c != null) {
	 	                        RentalRecord record = new RentalRecord(v, c, date, amount, type);
	 	                        rentalHistory.addRecord(record);
	 	                    }
	 	                }
	 	            }
	 	       } catch (Exception e) {
	 	            System.out.println("No rental records loaded: " + e.getMessage());
	 	        }
	 	    }


	    
	    private void saveVehicle(Vehicle vehicle) {
	        try (FileWriter writer = new FileWriter("vehicles.txt", true)) {
	            writer.write(vehicle.getLicensePlate() + "," +
	                         vehicle.getMake() + "," +
	                         vehicle.getModel() + "," +
	                         vehicle.getYear() + "," +
	                         vehicle.getStatus() + System.lineSeparator());
	        } catch (IOException e) {
	            System.out.println("Error saving vehicle: " + e.getMessage());
	        }
	    }

	    private void saveCustomer(Customer customer) {
	        try (FileWriter writer = new FileWriter("customers.txt", true)) {
	            writer.write(customer.getCustomerId() + "," +
	                         customer.getCustomerName() + System.lineSeparator());
	        } catch (IOException e) {
	            System.out.println("Error saving customer: " + e.getMessage());
	        }
	    }
	    private void saveRecord(RentalRecord record) {
	        try (FileWriter writer = new FileWriter("rental_records.txt", true)) {
	            writer.write(record.getRecordType() + "," +
	                         record.getVehicle().getLicensePlate() + "," +
	                         record.getCustomer().getCustomerName() + "," +
	                         record.getRecordDate().toString() + "," +
	                         record.getTotalAmount() + System.lineSeparator());
	        } catch (IOException e) {
	            System.out.println("Error saving rental record: " + e.getMessage());
	        }
	    }


    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Duplicate vehicle: License plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (findCustomerById(customer.getCustomerId()) != null) {
            System.out.println("Duplicate customer: ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }


    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record); 
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);  
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }
   

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
}