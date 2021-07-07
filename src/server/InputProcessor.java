package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputProcessor{
    private static final Map<String, String> InfoRegexMap = Map.of("email", "^(.+)@(.+)$");

    public static String takeValidInput(String key) {
        String info = null;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                info = br.readLine();
                if (isValid(info, InfoRegexMap.get(key))) {
                    break;
                } else {
                    System.out.println("Please enter a valid " + key + ". \nTry again:");
                    continue;
                }
            }

        } catch (IOException e) {
        }

        return info;
    }

    private static boolean isValid(String input, String regex){
        
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(input);

        return m.matches();
    }

    public static void main(String[] args) {

        System.out.println("Please enter your email address");
        String userEmail = InputProcessor.takeValidInput("email");

        System.out.println("This is your email " + userEmail);
        
    }

}