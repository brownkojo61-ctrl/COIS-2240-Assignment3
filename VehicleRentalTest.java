import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;


public class VehicleRentalTest {

    @Test
    void testLicensePlate() {
        
        Vehicle v1 = new Vehicle("Make", "Model", 2019);
        v1.setLicensePlate("AAA100");
        assertEquals("AAA100", v1.getLicensePlate());

        Vehicle v2 = new Vehicle("Make", "Model", 2020);
        v2.setLicensePlate("ABC567");
        assertEquals("ABC567", v2.getLicensePlate());

        Vehicle v3 = new Vehicle("Make", "Model", 2021);
        v3.setLicensePlate("ZZZ999");
        assertEquals("ZZZ999", v3.getLicensePlate());

        
        Vehicle v4 = new Vehicle("Make", "Model", 2022);
        assertThrows(IllegalArgumentException.class, () -> v4.setLicensePlate(""));   

        Vehicle v5 = new Vehicle("Make", "Model", 2023);
        assertThrows(IllegalArgumentException.class, () -> v5.setLicensePlate(null));

        Vehicle v6 = new Vehicle("Make", "Model", 2024);
        assertThrows(IllegalArgumentException.class, () -> v6.setLicensePlate("AAA1000")); 

        Vehicle v7 = new Vehicle("Make", "Model", 2025);
        assertThrows(IllegalArgumentException.class, () -> v7.setLicensePlate("ZZZ99"));   
    }
    @Test
    void testRentAndReturnVehicle() {

        Vehicle car = new Vehicle("Toyota", "Corolla", 2019);
        car.setLicensePlate("AAA100");
        Customer customer = new Customer(1, "George");

        RentalSystem rentalSystem = RentalSystem.getInstance();
        rentalSystem.addVehicle(car);
        rentalSystem.addCustomer(customer);
        
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        boolean rentSuccess = rentalSystem.rentVehicle(car, customer, java.time.LocalDate.now(), 200.0);
        assertTrue(rentSuccess);
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());
     
        boolean rentAgain = rentalSystem.rentVehicle(car, customer, java.time.LocalDate.now(), 200.0);
        assertFalse(rentAgain);

        
        boolean returnSuccess = rentalSystem.returnVehicle(car, customer, java.time.LocalDate.now(), 50.0);
        assertTrue(returnSuccess);
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());


        boolean returnAgain = rentalSystem.returnVehicle(car, customer, java.time.LocalDate.now(), 50.0);
        assertFalse(returnAgain);
    }
        
    @Test
    void testSingletonRentalSystem() throws Exception {
        
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
      
        int modifiers = constructor.getModifiers();
        assertEquals(Modifier.PRIVATE, modifiers, "RentalSystem constructor should be private");
       
        RentalSystem instance = RentalSystem.getInstance();
        
        assertNotNull(instance, "RentalSystem.getInstance() should return a non-null instance");
        
        RentalSystem anotherInstance = RentalSystem.getInstance();
        assertSame(instance, anotherInstance, "RentalSystem should enforce Singleton behavior");

        }

}