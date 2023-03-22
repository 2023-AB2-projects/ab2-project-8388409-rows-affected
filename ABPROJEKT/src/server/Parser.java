package server;

public class Parser {
    public Parser(String input) {

        System.out.println("Parser : " + input);
        if (input.contains("CREATE DATABASE")) {
            System.out.println("CREATE DATABASE");
            String[] split = input.split(" ");
            String databaseName = split[2];
            new CreateDatabase(databaseName);
        }
        if (input.contains("DROP DATABASE")) {
            System.out.println("DROP DATABASE");
            String[] split = input.split(" ");
            String databaseName = split[2];
            new DropDatabase(databaseName);
        }
    }
}
