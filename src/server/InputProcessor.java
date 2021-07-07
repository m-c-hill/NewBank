package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This is a utility class to help with taking and validating user input.
// 
// You can call InputProcessor(String key) to take and validate an input value that matches the provided the key and then assigning the return value to a variable
// For example, InputProcessor("date") will look through the map to get the regex defining the date format  
public class InputProcessor{
    // Mapping each key to its value (regex)
    // Add the rest of the keys with their relevant regexes to this Map
    private static final Map<String, String> InfoRegexMap = Map.of(
        "email", "^(.+)@(.+)$",
        "date", "^\\d{2}/\\d{2}/\\d{4}$"
        );

    // Method to take and validate input
    public static String takeValidInput(String key) {
        String info = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                info = br.readLine();
                // Check if the provided input is valid by comparing its format with the relevant regex
                // Upon matching, break and return the given input
                if (isValid(info, InfoRegexMap.get(key))) {
                    break;
                } else {
                    // Display a message if the input is invalid. Loop again to take a new input
                    System.out.println("Please enter a valid " + key + ". \nTry again:");
                    continue;
                }
            }

        } catch (IOException e) {
        }

        return info;
    }

    // Method Overload
    // public static String takeValidInput(String key, String oldPass)

    // Input validation method
    private static boolean isValid(String input, String regex){
        // Extract the regex patter
        Pattern p = Pattern.compile(regex);
        // Compare it to the given input
        Matcher m = p.matcher(input);

        // Return true if there's a match and false if there isn't
        return m.matches();
    }

    // Main method for unit testing (can be deleted before making the final submission)
    public static void main(String[] args) {

        // System.out.println("Please enter your email address");
        // String userEmail = InputProcessor.takeValidInput("email");

        System.out.println("Please enter your date of birth");
        String birthDate = InputProcessor.takeValidInput("date");

        System.out.println(birthDate);
        
    }

}