package ui;

import model.AuthData;
import model.UserData;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PreLoginUI {
    private final ServerFacade facade;
    private AuthData auth;

    public PreLoginUI(ServerFacade facade) {
        this.facade = facade;
    }

    public void run() {
        help();
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            //printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if(result.equals("postLogin")) {
                    result = new PostLoginUI(facade, auth).run();
                    //result = "";
                }
            } catch (Throwable e) {
                var msg = e.toString();
                System.out.print(msg);
            }
        }
        System.out.println();

    }

    public String help() {
        System.out.println( EscapeSequences.SET_TEXT_COLOR_BLUE + """
                help - list possible commands
                register <username> <password> <email> - register a new user
                login <username> <password> - login an existing user
                quit - quit chess
                """);
        return "";
    }

    public String eval(String input) {
        try{
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens, 1, tokens.length);
            return switch(cmd) {
                case "quit" -> "quit";
                case "login" -> login(params);
                case "register" -> register(params);
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String login(String... params) throws Exception {
        try {
            if (params.length == 0) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Not enough parameters");
            }
            else if (params.length == 2) {
                var user = new UserData(params[0], params[1], "");
                auth = facade.login(user);
                System.out.printf("%sYou logged in as %s.%n", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, params[0]);
                return "postLogin";
            }
            else{
                System.out.println("Too many parameters");
                return "";}
        } catch(Exception ex) {
            System.out.println("Could not login; username or password invalid");
            throw new Exception("Login failed");
        }
        return "";
    }

    public String register(String... params) throws Exception {
        try{
            if (params.length == 0) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Not enough parameters");
            }
            else if (params.length == 3) {
                auth = facade.register(params[0], params[1], params[2]);
                String.format("%sYou registered and logged in as %s.%n", EscapeSequences.SET_TEXT_COLOR_LIGHT_GREY, params[0]);
                return "postLogin";
            }
            else {
                System.out.println("Too many parameters.");
                return "";}
            return "";

        }catch (Exception ex) {
            System.out.println("Could not register - please use a unique username");
            throw new Exception("Invalid register\n");
        }
    }
}
