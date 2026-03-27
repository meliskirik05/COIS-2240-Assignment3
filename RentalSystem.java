import java.util.List;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class RentalSystem {
	private static RentalSystem instance;
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();
    private RentalSystem() {
    	loadData();
    	
    }
    
    public static RentalSystem getInstance() {
    	if (instance == null) {
    		instance = new RentalSystem();
    	}
    	return instance;
    }

    public boolean addVehicle(Vehicle vehicle) {
    	if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
    		System.out.println("Error: A vehicle with this license plate already exists!");
    		return false;
    	}
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }
    public void saveVehicle(Vehicle vehicle) {
        try (PrintWriter out = new PrintWriter(new FileWriter("vehicles.txt", true))) {
            // Saving details separated by commas (CSV format)
            out.println(vehicle.getClass().getSimpleName() + "," + vehicle.getLicensePlate() + "," + 
                        vehicle.getMake() + "," + vehicle.getModel() + "," + vehicle.getYear());
        } catch (IOException e) {
            System.out.println("Error: Could not save vehicle data - " + e.getMessage());
        }
    }
    public void saveCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new FileWriter("customers.txt", true))) {
            out.println(customer.getCustomerId() + "," + customer.getCustomerName());
        } catch (IOException e) {
            System.out.println("Error: Could not save customer data - " + e.getMessage());
        }
    }

    public void saveRecord(RentalRecord record) {
        try (PrintWriter out = new PrintWriter(new FileWriter("rental_records.txt", true))) {
            out.println(record.getVehicle().getLicensePlate() + "," + 
                        record.getCustomer().getCustomerId() + "," + 
                        record.getRecordDate() + "," + 
                        record.getTotalAmount() + "," + 
                        record.getRecordType());
        } catch (IOException e) {
            System.out.println("Error: Could not save rental record - " + e.getMessage());
        }
    }
    public void loadData() {
        try {
            File vFile = new File("vehicles.txt");
            if (vFile.exists()) {
                Scanner s = new Scanner(vFile);
                while (s.hasNextLine()) {
                    String[] p = s.nextLine().split(",");
                    Vehicle v;
                    
                    // Identify the vehicle type when reading from the file
                    if (p[0].equals("Car")) {
                        v = new Car(p[2], p[3], Integer.parseInt(p[4]), 4);
                    } else if (p[0].equals("Minibus")) {
                        v = new Minibus(p[2], p[3], Integer.parseInt(p[4]), true);
                    } else {
                        v = new PickupTruck(p[2], p[3], Integer.parseInt(p[4]), 1000.0, false);
                    }
                    
                    v.setLicensePlate(p[1]);
                    vehicles.add(v);
                }
                s.close();
            }
        } catch (Exception e) {
            System.out.println("Error: No saved data found or an issue occurred while loading.");
        }
    }
    // Task 1.3: Validating customer data before adding to list
    public boolean addCustomer(Customer customer) {
    	if (findCustomerById(customer.getCustomerId()) != null) {
    		System.out.println("Error: Customer ID" + customer.getCustomerId()+ " already exist.");
    		return false;
    	}
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        }
        else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        }
        else {
            System.out.println("Vehicle is not rented.");
            return false;
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