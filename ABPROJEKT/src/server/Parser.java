package server;

public class Parser {
    private Host host;
    private boolean parserError = false;
    public Parser(String input, Host host) {
        this.host = host;
        System.out.println("Parser : " + input);

        if (input.contains("USE")) {
            System.out.println("USE");
            String[] split = input.split(" ");
            String databaseName = split[1];

            new UseDatabase(databaseName,this);
            if (parserError) {
                parserError = false;
                host.setError("Database does not exist");
                host.setCurrentDatabase("");
            } else {
                host.setCurrentDatabase(databaseName);
            }
        }

        // CREATE DATABASE
        if (input.contains("CREATE DATABASE")) {
            System.out.println("CREATE DATABASE");
            String[] split = input.split(" ");
            String databaseName = split[2];
            new CreateDatabase(databaseName,this);
            if (parserError) {
                parserError = false;
                host.setError("Database already exists");
            }
        }

        // DROP DATABASE
        if (input.contains("DROP DATABASE")) {
            System.out.println("DROP DATABASE");
            String[] split = input.split(" ");
            String databaseName = split[2];
            new DropDatabase(databaseName);
        }

        // CREATE TABLE
        String currentDatabase = host.getCurrentDatabase();
        if (input.contains("CREATE TABLE")) {
            System.out.println("CREATE TABLE");
            String[] split = input.split(" ");
            String tableName = split[2];
            String contents = split[3];
            new CreateTable(tableName, currentDatabase, contents);
        }
    }
    public void setParserError(boolean parserError) {
        this.parserError = parserError;
    }
}
