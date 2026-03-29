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
        // Getting the singleton instance
        rentalSystem = RentalSystem.getInstance();
        
        // Creating a Car with 3 parameters as defined in your Vehicle class
        car = new Car("Toyota", "Corolla", 2022, 5); 
        car.setLicensePlate("ABC123");
        
        customer = new Customer(101, "Test User");
    }

    @Test
    void testLicensePlateValidation() {
        Vehicle testCar = new Car("Honda", "Civic", 2023, 5);
        
        // Testing if setting a plate works without errors
        assertDoesNotThrow(() -> testCar.setLicensePlate("AAA100"));
    }

    @Test
    void testRentAndReturnVehicle() {
        // Initial state check
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
        
        // Testing rentVehicle with your specific parameters: (vehicle, customer, date, amount)
        boolean rentResult = rentalSystem.rentVehicle(car, customer, LocalDate.now(), 50.0);
        assertTrue(rentResult);
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());
        
        // Testing returnVehicle with your specific parameters: (vehicle, customer, date, extraFees)
        boolean returnResult = rentalSystem.returnVehicle(car, customer, LocalDate.now(), 0.0);
        assertTrue(returnResult);
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
    }

    @Test
    void testSingletonRentalSystem() throws Exception {
        // Validating that the constructor is private (Singleton Pattern)
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        int modifiers = constructor.getModifiers();
        assertEquals(Modifier.PRIVATE, modifiers, "Constructor must be private for Singleton pattern");
        
        // Ensuring getInstance returns the same object
        RentalSystem instance1 = RentalSystem.getInstance();
        RentalSystem instance2 = RentalSystem.getInstance();
        assertSame(instance1, instance2, "Both instances should be the same");
    }
}