package server;

public class Parser {
    private Host host;

    public Parser(String input, Host host) {
        this.host = host;
        System.out.println("Parser : " + input);

        if (input.contains("USE")) {
            System.out.println("USE");
            String[] split = input.split(" ");
            String databaseName = split[1];

            boolean dbExists = new UseDatabase().databaseExists(databaseName);
            if (dbExists) {
                host.setCurrentDatabase(databaseName);
                System.out.println("databaseName: " + databaseName);
            } else {
                host.setError("Database does not exist");
            }
        }
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

        String currentDatabase = host.getCurrentDatabase();

        if (input.contains("CREATE TABLE")) {
            System.out.println("CREATE TABLE");
            String[] split = input.split(" ");
            String tableName = split[2];
            String contents = split[3];
            new CreateTable(tableName, currentDatabase, contents);
        }
    }
}
