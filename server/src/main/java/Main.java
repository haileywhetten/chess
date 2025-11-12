import server.Server;

import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.run(8080);

        System.out.println("♕ 240 Chess Server");

        while (true) {
            System.out.printf("Type your numbers%n>>> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            var numbers = line.split(" ");
            System.out.println(Arrays.toString(numbers));
        }
    }
}