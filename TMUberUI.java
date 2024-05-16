//Name: Adan Khalid  Student ID: 501246496
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

// Simulation of a Simple Command-line based Uber App 

// This system supports "ride sharing" service and a delivery service

public class TMUberUI
{
  public static void main(String[] args)
  {
    // Create the System Manager - the main system code is in here 

    TMUberSystemManager tmuber = new TMUberSystemManager();
    
    Scanner scanner = new Scanner(System.in);
    System.out.print(">");

    // Process keyboard actions
    while (scanner.hasNextLine())
    {
      String action = scanner.nextLine();

      if (action == null || action.equals("")) 
      {
        System.out.print("\n>");
        continue;
      }
      // Quit the App
      else if (action.equalsIgnoreCase("Q") || action.equalsIgnoreCase("QUIT"))
        return;
      // Print all the registered drivers
      else if (action.equalsIgnoreCase("DRIVERS"))  // List all drivers
      {
        tmuber.listAllDrivers(); 
      }
      // Print all the registered users
      else if (action.equalsIgnoreCase("USERS"))  // List all users
      {
        tmuber.listAllUsers(); 
      }
      // Print all current ride requests or delivery requests
      else if (action.equalsIgnoreCase("REQUESTS"))  // List all requests
      {
        tmuber.listAllServiceRequests(); 
      }
      // Register a new driver
      else if (action.equalsIgnoreCase("REGDRIVER")) 
      { 
    	  try {
    		  String name = "";
          String carModel = "";
          String license = "";
          String driverAddress = "";
          System.out.print("Name: ");
          if (scanner.hasNextLine())
          {
            name = scanner.nextLine();
          }
          System.out.print("Car Model: ");
          if (scanner.hasNextLine())
          {
            carModel = scanner.nextLine();
          }
          System.out.print("Car License: ");
          if (scanner.hasNextLine())
          {
            license = scanner.nextLine();
          }
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            driverAddress = scanner.nextLine();
          }
          tmuber.registerNewDriver(name, carModel, license, driverAddress);
          System.out.printf("Driver: %-15s Car Model: %-15s License Plate: %-10s Address: %-10s", name, carModel, license, driverAddress); 
        } catch (Exception exception) {
    	  System.out.println(exception.getMessage());
        } 
      }
      // Register a new user
      else if (action.equalsIgnoreCase("REGUSER")) 
      { 
    	  try 
        {
    		  String name = "";
          String address = "";
          double wallet = 0.0;
    		  System.out.print("Name: ");
          if (scanner.hasNextLine())
          {
            name = scanner.nextLine();
          }
          System.out.print("Address: ");
          if (scanner.hasNextLine())
          {
            address = scanner.nextLine();
          }
          System.out.print("Wallet: ");
          if (scanner.hasNextDouble())
          {
            wallet = scanner.nextDouble();
            scanner.nextLine();
          }
          tmuber.registerNewUser(name, address, wallet);
          System.out.printf("User: %-15s Address: %-15s Wallet: %2.2f", name, address, wallet);
    	  } catch (Exception exception) {
    		System.out.println(exception.getMessage());
    	  } 
      }
      else if (action.equalsIgnoreCase("DROPOFF")) 
      {
        String idDriver = "";
        System.out.println("Please enter Driver ID: ");
        if (scanner.hasNextLine())
        {
          idDriver = scanner.nextLine();
          scanner.nextLine();
        }
        try 
        {
        	tmuber.dropOff(idDriver);
			    System.out.println("Driver: " + idDriver + " Is Currently Dropping Off");
        } catch (Exception exception) {
        	System.out.println(exception.getMessage());
        }
           
      }

      else if (action.equalsIgnoreCase("LOADDRIVERS")) {
        String fileName = "";

        try {
          System.out.print("Please Enter Driver Text File (remember to add .txt to the end): ");
          if (scanner.hasNextLine()) {
    			  fileName = scanner.nextLine();
    		  }
          ArrayList<Driver> haveLoaded = TMUberRegistered.loadPreregisteredDrivers(fileName);
          tmuber.setDrivers(haveLoaded);
          System.out.println("Drivers Have Been Loaded Into The System");
          } catch (FileNotFoundException exception) {
            System.out.println(fileName + " Not Found");
          } catch (IOException exception) {
            System.out.println("An error has occured when reading the file. Exiting the program... ");
            System.exit(1);
          }
        
      }

      else if (action.equalsIgnoreCase("LOADUSERS")) {
    	  String fileName = "";
    	  try {
          
          System.out.print("Please Enter User Text File (remember to add .txt to the end): ");
              
    		  if (scanner.hasNextLine()) {
    			  fileName = scanner.nextLine();
    		  }
    		  ArrayList<User> haveLoaded = TMUberRegistered.loadPreregisteredUsers(fileName);
    		  tmuber.setUsers(haveLoaded);
    		  System.out.println("Users Have Been Loaded Into The System");
    	  } catch (FileNotFoundException exception) {
          System.out.println(fileName + " Not Found");
        } catch (IOException exception) {
          System.out.println("An error has occured when reading the file. Exiting the program... ");
          System.exit(1);
        } 	  
      }

      else if (action.equalsIgnoreCase("DRIVETO")) {
    	  String idDriver = "";
        String address = "";
        System.out.print("Please enter Driver ID: ");
        if (scanner.hasNextLine())
        {
          idDriver = scanner.nextLine();
        }
        System.out.print("Address: ");
        if (scanner.hasNextLine())
        {
          address = scanner.nextLine();
        }
        tmuber.driveTo(idDriver, address);
      }

      else if (action.equalsIgnoreCase("PICKUP")) {
     	  String idDriver = "";
        System.out.print("Please enter Driver ID: ");
        if (scanner.hasNextLine())
        {
          idDriver = scanner.nextLine();
        }
     	  tmuber.pickup(idDriver);
      }
      // Request a ride
      else if (action.equalsIgnoreCase("REQRIDE")) 
      {
        String account = "";
        System.out.print("User Account Id: ");
        if (scanner.hasNextLine())
        {
          account = scanner.nextLine();
        }
        String from = "";
        System.out.print("From Address: ");
        if (scanner.hasNextLine())
        {
          from = scanner.nextLine();
        }
        String to = "";
        System.out.print("To Address: ");
        if (scanner.hasNextLine())
        {
          to = scanner.nextLine();
        }
        try {
        	tmuber.requestRide(account, from, to);
          User user = tmuber.getUser(account);
          System.out.printf("\nRIDE for: %-15s From: %-15s To: %-15s", user.getName(), from, to);
        } catch (Exception exception) {
        	System.out.println(exception.getMessage());
        }
      }
      // Request a food delivery
      else if (action.equalsIgnoreCase("REQDLVY")) 
      {
        String account = "";
        System.out.print("User Account Id: ");
        if (scanner.hasNextLine())
        {
          account = scanner.nextLine();
        }
        String from = "";
        System.out.print("From Address: ");
        if (scanner.hasNextLine())
        {
          from = scanner.nextLine();
        }
        String to = "";
        System.out.print("To Address: ");
        if (scanner.hasNextLine())
        {
          to = scanner.nextLine();
        }
        String restaurant = "";
        System.out.print("Restaurant: ");
        if (scanner.hasNextLine())
        {
          restaurant = scanner.nextLine();
        }
        String foodOrder = "";
        System.out.print("Food Order #: ");
        if (scanner.hasNextLine())
        {
          foodOrder = scanner.nextLine();
        }
        try {
        	tmuber.requestDelivery(account, from, to, restaurant, foodOrder);
          User user = tmuber.getUser(account);
          System.out.printf("\nDELIVERY for: %-15s From: %-15s To: %-15s", user.getName(), from, to);  
            
            
        } catch (Exception exception) {
          System.out.println(exception.getMessage());	
        }
      }
      // Sort users by name
      else if (action.equalsIgnoreCase("SORTBYNAME")) 
      {
        tmuber.sortByUserName();
      }
      // Sort users by number of ride they have had
      else if (action.equalsIgnoreCase("SORTBYWALLET")) 
      {
        tmuber.sortByWallet();
      }
      // Sort current service requests (ride or delivery) by distance
      else if (action.equalsIgnoreCase("SORTBYDIST")) 
      {
        tmuber.sortByDistance();
      }
      // Cancel a current service (ride or delivery) request
      else if (action.equalsIgnoreCase("CANCELREQ")) 
      {
        int zoneNumber = -1;
        int requestNumber = -1;
        System.out.print("Zone Number: ");
        if (scanner.hasNextLine()) {
          zoneNumber = Integer.parseInt(scanner.nextLine());
        }
        System.out.print("Request Number: ");
        if (scanner.hasNextLine()) {
          requestNumber = Integer.parseInt(scanner.nextLine());
        }
        try
        {
          tmuber.cancelServiceRequest(zoneNumber, requestNumber);
          System.out.println("Service request #" + requestNumber + " has been cancelled");
        } catch (Exception e){
          System.out.println(e.getMessage());
        }
            
      }
      // Get the Current Total Revenues
      else if (action.equalsIgnoreCase("REVENUES")) 
      {
        System.out.println("Total Revenue: " + tmuber.totalRevenue);
      }
      // Unit Test of Valid City Address 
      else if (action.equalsIgnoreCase("ADDR")) 
      {
        String address = "";
        System.out.print("Address: ");
        if (scanner.hasNextLine())
        {
          address = scanner.nextLine();
        }
        System.out.print(address);
        if (CityMap.validAddress(address))
          System.out.println("\nValid Address"); 
        else
          System.out.println("\nBad Address"); 
      }
      // Unit Test of CityMap Distance Method
      else if (action.equalsIgnoreCase("DIST")) 
      {
        String from = "";
        System.out.print("From: ");
        if (scanner.hasNextLine())
        {
          from = scanner.nextLine();
        }
        String to = "";
        System.out.print("To: ");
        if (scanner.hasNextLine())
        {
          to = scanner.nextLine();
        }
        System.out.print("\nFrom: " + from + " To: " + to);
        System.out.println("\nDistance: " + CityMap.getDistance(from, to) + " City Blocks");
      }
      
      System.out.print("\n>");
    }
  }
}

