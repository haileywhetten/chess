package ui;

import model.UserData;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade facade;

    public PreLoginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        System.out.println(help());
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            //printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(result);
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

    }

    public String help() {
        return EscapeSequences.SET_TEXT_COLOR_BLUE + """
                help - list possible commands
                register <username> <password> <email> - register a new user
                login <username> <password> - login an existing user
                quit - quit chess
                """;
    }

    public String eval(String input) {
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "quit" -> "quit";
                case "help" -> help();
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws Exception {
        if (params.length == 0) {
            System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Please enter a username and a password to login");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] newInfo = line.toLowerCase().split(" ");
            if (newInfo.length == 2) {
                var user = new UserData(newInfo[0], newInfo[1], "");
                facade.login(user);
                return String.format("%sYou logged in as %s.%n", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, user.username());
            }
        }
        else if (params.length >= 2) {
            var user = new UserData(params[0], params[1], "");
            facade.login(user);
            return String.format("%sYou logged in as %s.%n", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, params[0]);
        }
        else{throw new Exception("Please enter a valid username and password.");}
        return null;
    }

    public String register(String... params) throws Exception {
        try{
            if (params.length == 0) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Please enter a username, password, and email to register");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                String[] newInfo = line.toLowerCase().split(" ");
                if (newInfo.length == 3) {
                    facade.register(newInfo[0], newInfo[1], newInfo[2]);
                    return String.format("%sYou registered and logged in as %s.%n", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, newInfo[0]);
                }
            }
            else if (params.length >= 3) {
                facade.register(params[0], params[1], params[2]);
                return String.format("%sYou registered and logged in as %s.%n", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, params[0]);
            }
            else {throw new Exception("Please enter a valid username and password.");}
            return null;

        }catch (Exception ex) {
            throw new Exception("Invalid register\n");
        }
    }
}
