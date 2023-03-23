package server;

import server.commands.*;

public class Parser {
    private Host host;
    private boolean parserError = false;
    private String otherError = "";
    public Parser(String input, Host host) {
        this.host = host;
        System.out.println("Parser : " + input);

        // USE
        if (input.contains("USE")) {
            System.out.println("USE");
            String[] split = input.split(" ");
            if (split.length <= 1) {
                host.setError("Invalid syntax");
                return;
            }
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
            if (split.length <= 2) {
                host.setError("Invalid syntax");
                return;
            }
            String databaseName = split[2];
            new CreateDatabase(databaseName,this);
            if (parserError) {
                parserError = false;
                host.setError("Database already exists");
            }
            else {
                new UseDatabase(databaseName,this);
            }
        }

        // DROP DATABASE
        if (input.contains("DROP DATABASE")) {
            System.out.println("DROP DATABASE");
            String[] split = input.split(" ");
            if (split.length <= 2) {
                host.setError("Invalid syntax");
                return;
            }
            String databaseName = split[2];
            new DropDatabase(databaseName);
        }

        // CREATE TABLE
        String currentDatabase = host.getCurrentDatabase();
        if (input.contains("CREATE TABLE")) {
            System.out.println("CREATE TABLE");
            String[] split = input.split(" ");
            if (split.length <= 2) {
                host.setError("Invalid syntax");
                return;
            }

            String tableName = split[2];
            StringBuilder contents = new StringBuilder();
            for (int i = 3; i < split.length; i++) {
                contents.append(split[i]).append(" ");
            }

            new CreateTable(tableName, currentDatabase, contents.toString(),this);

            if (otherError.equals("")) {
                host.setError("");
            } else {
                host.setError(otherError);
                otherError = "";
            }
        }

        // DROP TABLE
        if (input.contains("DROP TABLE")) {
            System.out.println("DROP TABLE");
            String[] split = input.split(" ");
            if (split.length <= 2) {
                host.setError("Invalid syntax");
                return;
            }

            String tableName = split[2];
            new DropTable(tableName, currentDatabase, this);
            if (otherError.equals("")) {
                host.setError("");
            } else {
                host.setError(otherError);
                otherError = "";
            }
        }
    }
    public void setParserError(boolean parserError) {
        this.parserError = parserError;
    }
    public void setOtherError(String otherError) {
        this.otherError = otherError;
    }
}
