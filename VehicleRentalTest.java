import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

public class VehicleRentalTest {
    private RentalSystem rentalSystem;
    private Vehicle car;
    private Customer customer;

    @BeforeEach
    void setUp() {
        // Get the singleton instance for testing
        rentalSystem = RentalSystem.getInstance();
        
        // Creating sample objects for the tests
        car = new Car("Toyota", "Corolla", 2022, 5);
        car.setLicensePlate("ABC123");
        
        customer = new Customer(101, "Test User");
    }

    // Task 2.1: Vehicle License Plate Validation
    @Test
    void testLicensePlateValidation() {
        Vehicle testCar = new Car("Honda", "Civic", 2023, 5);
        
        // Testing valid plates
        assertDoesNotThrow(() -> testCar.setLicensePlate("AAA100"));
        assertDoesNotThrow(() -> testCar.setLicensePlate("ABC567"));
        assertDoesNotThrow(() -> testCar.setLicensePlate("ZZZ999"));
        
        // Testing invalid plates (should throw IllegalArgumentException)
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate("AAA1000"));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate("ZZZ99"));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> testCar.setLicensePlate(null));
    }

    // Task 2.2: Rent/Return Vehicle Validation
    @Test
    void testRentAndReturnVehicle() {
        // Check if vehicle is initially available
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
        
        // Test renting the vehicle
        boolean rentResult = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 50.0);
        assertTrue(rentResult);
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());
        
        // Try renting the same vehicle again (should fail)
        assertFalse(rentalSystem.rentVehicle(car, customer, LocalDate.now(), 50.0));
        
        // Test returning the vehicle
        boolean returnResult = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
        assertTrue(returnResult);
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
        
        // Try returning the same vehicle again (should fail)
        assertFalse(rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0));
    }

    // Task 2.3: Singleton Validation using Reflection
    @Test
    void testSingletonRentalSystem() throws Exception {
        // Get the constructor using reflection
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        
        // Check if the constructor is private
        int modifiers = constructor.getModifiers();
        assertEquals(Modifier.PRIVATE, modifiers, "Constructor must be private for Singleton pattern");
        
        // Ensure the instance is not null and it's the same object
        RentalSystem instance1 = RentalSystem.getInstance();
        assertNotNull(instance1);
        
        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance1, instance2, "Both instances should be the same");
    }
}