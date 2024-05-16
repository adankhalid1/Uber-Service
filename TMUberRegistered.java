//Name: Adan Khalid  Student ID: 501246496
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TMUberRegistered
{
    // These variables are used to generate user account and driver ids
    private static int firstUserAccountID = 900;
    private static int firstDriverId = 700;

    // Generate a new user account id
    public static String generateUserAccountId(ArrayList<User> current)
    {
        return "" + firstUserAccountID + current.size();
    }

    // Generate a new driver id
    public static String generateDriverId(ArrayList<Driver> current)
    {
        return "" + firstDriverId + current.size();
    }

    // Database of Preregistered users
    // In Assignment 2 these will be loaded from a file
    // The test scripts and test outputs included with the skeleton code use these
    // users and drivers below. You may want to work with these to test your code (i.e. check your output with the
    // sample output provided). 
    public static ArrayList<User> loadPreregisteredUsers(String fileName) throws IOException {
        ArrayList<User> users = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String lineReader;

        while ((lineReader = reader.readLine()) != null) {
            String accountIdUser = generateUserAccountId(users);
            String nameUser = lineReader.trim();
            String addressUser = reader.readLine().trim();
            double walletAmountUser = Double.parseDouble(reader.readLine().trim());
            User newUser = new User(accountIdUser, nameUser, addressUser, walletAmountUser);
            users.add(newUser);
        }
        return users;

    }

    // Database of Preregistered users
    // In Assignment 2 these will be loaded from a file
    public static ArrayList<Driver> loadPreregisteredDrivers(String fileName) throws IOException {
        ArrayList<Driver> drivers = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String lineReader;

        while ((lineReader = reader.readLine()) != null) {
            String driverId = generateDriverId(drivers);
            String nameDriver = lineReader.trim();
            String carModelDriver = reader.readLine().trim();
            String carLicenseDriver = reader.readLine().trim();
            String addressDriver = reader.readLine().trim();
            Driver newDriver = new Driver(driverId, nameDriver, carModelDriver, carLicenseDriver, addressDriver);
            drivers.add(newDriver);
        }
        return drivers;

    }
}

