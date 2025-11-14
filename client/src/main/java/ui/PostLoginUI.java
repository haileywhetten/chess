package ui;

import model.*;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade facade;
    private AuthData auth;

    public PostLoginUI(ServerFacade facade, AuthData authData) {
        this.facade = facade;
        auth = authData;
    }

    public String run() {
        System.out.println(help());
        Scanner scanner1 = new Scanner(System.in);
        var result = "";
        while (!result.equals("quit")) {
            String line = scanner1.nextLine();

            try {
                result = eval(line);
                if(result.equals("preLogin")) {
                    new PreLoginUI(facade).run();
                }
            } catch (Throwable ex) {
                var msg = ex.toString();
                System.out.print(msg);
            }
        }
        System.out.println();
        return result;

    }

    public String help() {
        return EscapeSequences.SET_TEXT_COLOR_BLUE + """
                help - list possible commands
                logout - when you're done playing
                create <name> - create a new game
                join <ID> <WHITE|BLACK> - join a game
                observe <ID> - observe a game
                list - list all the games
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
                case "logout" -> logout();
                case "create" -> "create";
                case "join" -> "join";
                case "observe" -> "observe";
                case "list" -> "list";
                default -> help();
            };
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    public String logout() throws Exception{
        try{
            facade.logout(auth);
            System.out.printf("%sYou logged out!%n", EscapeSequences.SET_TEXT_COLOR_YELLOW);
            return "preLogin";
        } catch(Exception ex) {
            throw new Exception("Logout failed");
        }
    }

    public String createGame(String... params) throws Exception {
        return null;
    }
}
