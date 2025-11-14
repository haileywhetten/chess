package ui;

import model.*;
import serverfacade.ServerFacade;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class PostLoginUI {
    private final ServerFacade facade;
    private final AuthData auth;
    private List<GameInfo> gameList = null;

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
                if(result.equals("gameplay")) {
                    System.out.println("this is where the gameplay ui will go");
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
                case "create" -> createGame(params);
                case "join" -> "join";
                case "observe" -> "observe";
                case "list" -> listGames();
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
        try{
            if (params.length == 0) {
                System.out.println(EscapeSequences.SET_TEXT_COLOR_GREEN + "Please enter a name to call your game:");
                Scanner scanner = new Scanner(System.in);
                String line = scanner.nextLine();
                String[] newInfo = line.toLowerCase().split(" ");
                if (newInfo.length >= 1) {
                    facade.createGame(newInfo[0], auth);
                    System.out.printf("%sYou successfully created the game %s.%n", EscapeSequences.SET_TEXT_COLOR_MAGENTA, newInfo[0]);
                    return "create";
                }
            }
            else {
                facade.createGame(params[0], auth);
                System.out.printf("%sYou successfully created the game %s.%n", EscapeSequences.SET_TEXT_COLOR_MAGENTA, params[0]);
                return "create";
            }
        } catch(Exception ex) {
            throw new Exception("Create game failed.");
        }
        return null;
    }

    public String listGames() throws Exception {
        try {
            gameList = facade.listGames(auth);
            if(gameList.isEmpty()) {
                System.out.printf("%sThere are no games to list :( try creating one!%n", EscapeSequences.SET_TEXT_COLOR_RED);
            }
            else {
                System.out.printf("%sHere is the list of games: %n", EscapeSequences.SET_TEXT_COLOR_RED);
                for (GameInfo game : gameList) {
                    System.out.printf("%s%d - %s%n", EscapeSequences.SET_TEXT_COLOR_BLUE, gameList.indexOf(game) + 1, game.gameName());
                }
            }
            return "list";
        } catch(Exception ex) {
            throw new Exception("Could not list the games");
        }
    }
}
