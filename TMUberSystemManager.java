//Name: Adan Khalid  Student ID: 501246496
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.List;
/*
 * 
 * This class contains the main logic of the system.
 * 
 *  It keeps track of all users, drivers and service requests (RIDE or DELIVERY)
 * 
 */
public class TMUberSystemManager
{

  private Map<String, User> users;

  private ArrayList<Driver> drivers;

  private ArrayList<TMUberService> serviceRequests;

  private Queue<TMUberService>[] serviceRequestsQueue = new LinkedList[4];


  public double totalRevenue; // Total revenues accumulated via rides and deliveries
  
  // Rates per city block
  private static final double DELIVERYRATE = 1.2;
  private static final double RIDERATE = 1.5;
  
  // Portion of a ride/delivery cost paid to the driver
  private static final double PAYRATE = 0.1;

  // These variables are used to generate user account and driver ids
  int userAccountId = 900;
  int driverId = 700;

  public TMUberSystemManager()
  {
    users = new HashMap<>();
    serviceRequests = new ArrayList<>();
    drivers = new ArrayList<Driver>();
    serviceRequestsQueue = (Queue<TMUberService>[]) new LinkedList[4];
    for (int i=0; i < serviceRequestsQueue.length; i++){
      serviceRequestsQueue[i] = new LinkedList<>();
    } 
    
    try {
      TMUberRegistered.loadPreregisteredUsers("users.txt");
      TMUberRegistered.loadPreregisteredDrivers("drivers.txt");
    } catch (IOException e) {
      e.printStackTrace();
    }
    
     
    totalRevenue = 0;
  }

  // General string variable used to store an error message when something is invalid 
  // (e.g. user does not exist, invalid address etc.)  
  // The methods below will set this errMsg string and then return false
  String errMsg = null;

  public String getErrorMessage()
  {
    return errMsg;
  }
  
  // Generate a new user account id
  private String generateUserAccountId()
  {
    return "" + userAccountId + users.size();
  }
  
  // Generate a new driver id
  private String generateDriverId()
  {
    return "" + driverId + drivers.size();
  }

  public void setUsers(ArrayList<User> userList) 
  {  
	  users.clear();	  
	  for (User user : userList) {
		  users.put(user.getAccountId(), user);
	  }	  
  }

  // Given user account id, find user in list of users
  public User getUser(String accountId)
  {
    if (users.isEmpty()) {
		  return null;
	  }
	  return users.get(accountId);
  }
  
  // Check for duplicate user
  private void userExists(User user)
  {
    // Simple way
    // return users.contains(user);
    if (users.containsKey(user.getAccountId())) 
    {
    	throw new UserAlreadyExistsException("This User Already Exists Within The System");
    }
  }
  
  public void setDrivers(ArrayList<Driver> drivers) 
  {
	  this.drivers.clear();
	  this.drivers.addAll(drivers);    
  }

 // Check for duplicate driver
  private void driverExists(Driver driver)
 {
   // simple way
   // return drivers.contains(driver);
   
   for (int i = 0; i < drivers.size(); i++)
   {
     if (drivers.get(i).equals(driver))
     {
      throw new DriverAlreadyExistsException("This Driver Already Exists Within The System"); 	 
     }     
    }
  }
  
 
 // Given a user, check if user ride/delivery request already exists in service requests
 private void existingRequest(TMUberService req)
 {
   // Simple way
   // return serviceRequests.contains(req);
   
   for (int i = 0; i < serviceRequests.size(); i++)
   {
     if (serviceRequests.get(i).equals(req)) 
     {
      throw new ExistingRequestException("There Is An Existing Request Already Made Within The System");
     }
   }
 } 
 
  
  // Calculate the cost of a ride or of a delivery based on distance 
  private double getDeliveryCost(int distance)
  {
    return distance * DELIVERYRATE;
  }

  private double getRideCost(int distance)
  {
    return distance * RIDERATE;
  }

  // Go through all drivers and see if one is available
  // Choose the first available driver
  private Driver getAvailableDriver()
  {
    for (int i = 0; i < drivers.size(); i++)
    {
      Driver driver = drivers.get(i);
      if (driver.getStatus() == Driver.Status.AVAILABLE)
        return driver;
    }
    return null;
  }

  // Print Information (printInfo()) about all registered users in the system
  public void listAllUsers()
  {
    System.out.println();
    
    ArrayList<String> sortedKey1 = new ArrayList<>(users.keySet());
    Collections.sort(sortedKey1);
    
    for (int i = 0; i < sortedKey1.size(); i++) {
    	User user = users.get(sortedKey1.get(i));
    	user.printInfo();
    	System.out.println();
    }
  }

  // Print Information (printInfo()) about all registered drivers in the system
  public void listAllDrivers()
  {
    System.out.println();
    
    for (int i = 0; i < drivers.size(); i++)
    {
      int index = i + 1;
      System.out.printf("%-2s. ", index);
      drivers.get(i).printInfo();
      System.out.println(); 
    }
  }

  // Print Information (printInfo()) about all current service requests
  public void listAllServiceRequests()
  {
    for (int i = 0; i < serviceRequestsQueue.length; i++)
    {
      System.out.println("Current Zone: " + i);
      Queue<TMUberService> currentQueueZone = serviceRequestsQueue[i];
      int queueRequestNum = 1;
      
      if (!(currentQueueZone.size() == 0)) {
    	  for (TMUberService zoneService : currentQueueZone) {
    		  System.out.println(queueRequestNum + ".-------------------------------------");
    		  System.out.println(zoneService);
    		  System.out.println();
    		  queueRequestNum++;
    	  }
      } 
      else {
    	  System.out.println("========\n");
      }
      System.out.println();
    }
  }

  // Add a new user to the system
  public void registerNewUser(String name, String address, double wallet)
  {
    //Ensuring wallet has sufficient funds
    if (wallet < 0)
    {
    	throw new InvalidMoneyInWalletException("The User Wallet has Insufficient Funds.");
    }
    
    // Ensuring address is valid
    if (!CityMap.validAddress(address))
    {
    	throw new InvalidUserAddressException("The User Address is Invalid");
    }

    //Ensuring name is valid
    if (name == null || name.equals(""))
    {
    	throw new InvalidUserNameException("The UserName is Invalid");
    }
       
    
    User user = new User(generateUserAccountId(), name, address, wallet);

    // Check for duplicate user
    userExists(user);

    users.put(user.getAccountId(), user);  
  }

  // Add a new driver to the system
  public void registerNewDriver(String name, String carModel, String carLicencePlate, String address)
  {
    if (!(CityMap.validAddress(address)))
    {
    	throw new InvalidDriverAddressException("Driver Address is Invalid");
    }

    //Ensuring name is valid
    if (name == null || name.equals(""))
    {
    	throw new InvalidDriverNameException("Driver Name is Invalid");
    }
    
    // Ensuring car license plate is valid
    if (carLicencePlate == null || carLicencePlate.equals(""))
    {
    	throw new InvalidCarLicenseException("Car License Plate is Invalid");
    }

    //Ensuring the car model is valid
    if (carModel == null || carModel.equals(""))
    {
    	throw new InvalidCarModelException("The Car Model is Invalid");   
    }
    
    
    
    
    Driver driver = new Driver(generateDriverId(), name, carModel, carLicencePlate, address);
    
    // Check for duplicate driver. If not, add to drivers list
    driverExists(driver);
    drivers.add(driver);   
  }

  // Request a ride. User wallet will be reduced when drop off happens
  public void requestRide(String accountId, String from, String to)
  {
    // Check valid user account
    User user = getUser(accountId);
    // Check for a valid from and to addresses
    if (!CityMap.validAddress(from))
    {
    	throw new InvalidFromAddressException("'From' Address is Invalid");
    }

    if (!CityMap.validAddress(to))
    {
    	throw new InvalidToAddressException("'To' Address is Invalid");
    }

    if (user == null)
    {
    	throw new UserNotFoundException("User Cannot Be Found");
    }
    
    // Get distance for this ride
    int distance = CityMap.getDistance(from, to);
    // Distance == 1 or == 0 not accepted
    if (!(distance > 1))
    {
    	throw new InsufficientTravelDistanceException("Travel Distance is Insufficient");
    }
    // Check if user has sufficient funds in wallet for trip
    double costTotal = getRideCost(distance);
    if (user.getWallet() < costTotal)
    {
    	throw new InsufficientFundsException("Funds in Wallet are Insufficient");
    }
    // Get available driver
    Driver driver = getAvailableDriver();
    if (driver == null) 
    {
    	throw new NoDriversAvailableException("No Drivers are Currently Available");
    }
    // Create the request
    
    int cityZone = CityMap.getCityZone(from);
    
    boolean small = (cityZone < 0);
    boolean greaterLength = (cityZone >= serviceRequestsQueue.length);
    
    if (small || greaterLength) {
    	
    	throw new InvalidZoneException("City Zone is Invalid");
    	
    }
    
    TMUberRide uberRideNew = new TMUberRide(from, to, user, distance, costTotal);
    
    serviceRequestsQueue[cityZone].add(uberRideNew);
    
    // Check existing ride request for this user
    existingRequest(uberRideNew);
    serviceRequests.add(uberRideNew);
    user.addRide();
  }

  // Request a food delivery. User wallet will be reduced when drop off happens
  public void requestDelivery(String accountId, String from, String to, String restaurant, String foodOrderId)
  {
    // Check valid user account
    User user = getUser(accountId);
    // Check for a valid from and to addresses
    if (!CityMap.validAddress(from))
    {
    	throw new InvalidFromAddressException("'From' Address is Invalid");
    }

    if (!CityMap.validAddress(to))
    {
    	throw new InvalidToAddressException("'To' Address is Invalid");
    }

    if (user == null)
    {
    	throw new UserNotFoundException("User Cannot Be Found");
    }
    
    // Get distance for this ride
    int distance = CityMap.getDistance(from, to);

    if (distance == 0)
    {
    	throw new InsufficientTravelDistanceException("Travel Distance is Insufficient");
    }
    // Check if user has sufficient funds in wallet for trip
    double costTotal = getDeliveryCost(distance);
    if (user.getWallet() < costTotal)
    {
    	throw new InsufficientFundsException("Funds in Wallet are Insufficient");
    }
    // Get available driver
    Driver driver = getAvailableDriver();
    if (driver == null) 
    {
    	throw new NoDriversAvailableException("No Drivers are Currently Available");
    }

    // Create request
    int cityZone = CityMap.getCityZone(from);
    
    boolean small = (cityZone < 0);
    boolean greaterLength = (cityZone >= serviceRequestsQueue.length);
    
    if (small || greaterLength) {
    	
    	throw new InvalidZoneException("City Zone is Invalid");
    	
    }
    
    TMUberDelivery uberDeliveryNew = new TMUberDelivery(from, to, user, distance, costTotal, restaurant, foodOrderId);
    
    serviceRequestsQueue[cityZone].add(uberDeliveryNew);
    
    // Check existing ride request for this user
    existingRequest(uberDeliveryNew);
    serviceRequests.add(uberDeliveryNew);
    user.addDelivery();
  }

  public void driveTo(String idDriver, String Address) {
	  
	  Driver drivingToLocation = usingIdGetDriver(idDriver);
    if (drivingToLocation == null) 
    {
      throw new DriverNotFoundException("Driver not found for ID: " + idDriver);
    }
  
    if (drivingToLocation.getStatus() == Driver.Status.DRIVING) 
    {
      throw new DriverNotDrivingException("Driver is currently driving");
    }
    int driverZone = CityMap.getCityZone(Address);
	  boolean small = (driverZone < 0);
	  boolean notQueue = (driverZone >= serviceRequestsQueue.length);

    if ((drivingToLocation.getStatus() == Driver.Status.DRIVING)) {
		  throw new DriverNotDrivingException("Driver Currently Isn't Driving");
	  }
	  
	  boolean flagDriver = (drivingToLocation == null);
	  if (flagDriver) {
		  throw new DriverNotFoundException("Based On ID, Driver has not been found");
	  }
	  
    if (!(CityMap.validAddress(Address))) {
		  throw new InvalidAddressException("Invalid Address");
	  }
	  drivingToLocation.setAddress(Address);
    
	  
	  if (small || notQueue) {
		  throw new InvalidZoneException("Zone is Invalid");
	  }
	  drivingToLocation.setZone(driverZone); 
	  System.out.println("Driver: " + driverId + " Is Now Currently in Zone: " + driverZone);
  }


  // Cancel an existing service request. 
  // parameter request is the index in the serviceRequests array list
  public void cancelServiceRequest(int request, int zone)
  {
    int counter = 1;
    // Check if valid request #
	  Queue<TMUberService> zoneQ = serviceRequestsQueue[zone];
    Queue<TMUberService> cancellationQ = new LinkedList<>();
	  
	  if (zone < 0 || zone >= serviceRequestsQueue.length) 
    {	
	    throw new InvalidZoneException("City Zone is Invalid");
	  }
	  
	  if (request < 1 || request > zoneQ.size())
    {
    	throw new InvalidRequestNumberException("Request Number is Invalid");
    }
	  
	  for (TMUberService service : zoneQ) {
		  if (!(counter == request)) {
			
			  cancellationQ.add(service);
			
		  }
		counter++;
	  }
  }
  
  // Drop off a ride or a delivery. This completes a service.
  // parameter request is the index in the serviceRequests array list
  public void dropOff(String idDriver)
  {
    Driver droppingOff = usingIdGetDriver(idDriver);
    boolean flagDriver = (droppingOff == null);
	  if (flagDriver) {
		  throw new DriverNotFoundException("Based On ID, Driver has not been found");
	  }
	  
	  if (!(droppingOff.getStatus() == Driver.Status.DRIVING)) 
    {
		  throw new DriverNotDrivingException("Driver Currently Isn't Driving");
	  }
    TMUberService service = droppingOff.getService();
    User userService = service.getUser();
    String toAddress = service.getTo();
    int driverZone = CityMap.getCityZone(toAddress);
    boolean small = (driverZone < 0);
	  boolean notQueue = (driverZone >= serviceRequestsQueue.length);
    if (small || notQueue) {
		  throw new InvalidZoneException("Zone is Invalid");
	  }
           
	  totalRevenue += service.getCost();    
	  droppingOff.pay(service.getCost()*PAYRATE); 
	  totalRevenue -= service.getCost()*PAYRATE;
	  userService.payForService(service.getCost()); 
	  droppingOff.setStatus(Driver.Status.AVAILABLE); 
	  droppingOff.setService(null);
	  droppingOff.setAddress(toAddress);
	  droppingOff.setZone(driverZone);
  }

  public void pickup(String idDriver) {
	  
	  Driver pickingUp = usingIdGetDriver(idDriver);
    int driverZone = CityMap.getCityZone(pickingUp.getAddress());
	  boolean flagDriver = (pickingUp == null);
    Queue<TMUberService> zoneQueue = serviceRequestsQueue[driverZone];
    boolean small = (driverZone < 0);
	  boolean notInQ = (driverZone >= serviceRequestsQueue.length);

	  if (flagDriver) {
		  throw new DriverNotFoundException("Based On ID, Driver has not been found");
	  }
	  
	  if (small || notInQ) {
		  throw new InvalidZoneException("No Requests Currently in this zone");
	  }
	  
	  if (zoneQueue.size() == 0) {
		  System.out.println("No service requests available in Zone: " + driverZone);
		  return;
	  }
	  
	  TMUberService currentService = serviceRequestsQueue[driverZone].poll();
	  pickingUp.setService(currentService);
	  pickingUp.setStatus(Driver.Status.DRIVING);
	  pickingUp.setAddress(currentService.getFrom());
	  System.out.println("Driver is currently picking up in Zone: " + driverZone);  
  }

  public Driver usingIdGetDriver(String idDriver) {
	  for (Driver driver : drivers) 
    {
		  if (driver.getId().equals(idDriver)) 
      {
			  return driver;
		  }
	  } 
    return null;
  }


  // Sort users by name
  public void sortByUserName()
  {
    ArrayList<User> usersSort = new ArrayList<>(users.values()); 
    Collections.sort(usersSort, new NameComparator());
    listAllUsers();
  }

  private class NameComparator implements Comparator<User>
  {
    public int compare(User a, User b)
    {
      return a.getName().compareTo(b.getName());
    }
  }

  // Sort users by number amount in wallet
  public void sortByWallet()
  {
    ArrayList<User> usersSort = new ArrayList<>(users.values());
    Collections.sort(usersSort, new UserWalletComparator());
    listAllUsers();
  }

  private class UserWalletComparator implements Comparator<User>
  {
    public int compare(User a, User b)
    {
      if (a.getWallet() > b.getWallet()) return 1;
      if (a.getWallet() < b.getWallet()) return -1; 
      return 0;
    }
  }

  // Sort trips (rides or deliveries) by distance
  // class TMUberService must implement Comparable
  public void sortByDistance()
  {
    Collections.sort(serviceRequests);
    listAllServiceRequests();
  }
}

class NoDriversAvailableException extends RuntimeException {
	public NoDriversAvailableException(String string) {
		super(string);
	}
}

class UserAlreadyHasRideRequestException extends RuntimeException {
	public UserAlreadyHasRideRequestException(String string) {
		super(string);
	}
}

class UserAlreadyHasDeliveryRequestException extends RuntimeException {
	public UserAlreadyHasDeliveryRequestException(String string) {
		super(string);
	}
}



class InvalidDriverNameException extends RuntimeException {
	public InvalidDriverNameException(String string) {
		super(string);
	}
}

class InvalidCarModelException extends RuntimeException {
	public InvalidCarModelException(String string) {
		super(string);
	}
}

class InvalidCarLicenseException extends RuntimeException {
	public InvalidCarLicenseException(String string) {
		super(string);
	}
}

class InvalidDriverAddressException extends RuntimeException {
	public InvalidDriverAddressException(String string) {
		super(string);
	}
}

class UserAlreadyExistsException extends RuntimeException {
	public UserAlreadyExistsException(String string) {
		super(string);
	}
}

class DriverAlreadyExistsException extends RuntimeException {
	public DriverAlreadyExistsException(String string) {
		super(string);
	}
}

class UserDoesntExistsException extends RuntimeException {
	public UserDoesntExistsException(String string) {
		super(string);
	}
}

class InvalidUserNameException extends RuntimeException {
	public InvalidUserNameException(String string) {
		super(string);
	}
}

class InvalidMoneyInWalletException extends RuntimeException {
	public InvalidMoneyInWalletException(String string) {
		super(string);
	}
}

class InvalidToAddressException extends RuntimeException {
	public InvalidToAddressException(String string) {
		super(string);
	}
}

class InvalidUserAddressException extends RuntimeException {
	public InvalidUserAddressException(String string) {
		super(string);
	}
}

class DriverDoesntExistsException extends RuntimeException {
	public DriverDoesntExistsException(String string) {
		super(string);
	}
}

class ExistingRequestException extends RuntimeException {
	public ExistingRequestException(String string) {
		super(string);
	}
}

class UserNotFoundException extends RuntimeException {
	public UserNotFoundException(String string) {
		super(string);
	}
}

class InsufficientFundsException extends RuntimeException {
	public InsufficientFundsException(String string) {
		super(string);
	}
}

class InvalidRequestNumberException extends RuntimeException {
	public InvalidRequestNumberException(String string) {
		super(string);
	}
}

class InvalidZoneException extends RuntimeException {
	public InvalidZoneException(String string) {
		super(string);
	}
}

class InvalidAddressException extends RuntimeException {
	public InvalidAddressException(String string) {
		super(string);
	}
}

class NoRequestInQueueException extends RuntimeException {
	public NoRequestInQueueException(String string) {
		super(string);
	}
}

class DriverNotFoundException extends RuntimeException {
	public DriverNotFoundException(String string) {
		super(string);
	}
}

class InsufficientTravelDistanceException extends RuntimeException {
	public InsufficientTravelDistanceException(String string) {
		super(string);
	}
}

class DriverNotDrivingException extends RuntimeException {
	public DriverNotDrivingException(String string) {
		super(string);
	}
}

class CannotDriveToException extends RuntimeException {
	public CannotDriveToException(String string) {
		super(string);
	}
}

class InvalidFromAddressException extends RuntimeException {
	public InvalidFromAddressException(String string) {
		super(string);
	}
}




