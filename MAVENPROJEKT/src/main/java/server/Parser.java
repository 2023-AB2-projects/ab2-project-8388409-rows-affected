package server;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.commands.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Parser {
    private final Host host;
    private boolean parserError = false;
    private String otherError = "";
    public Parser(String input, Host host) {
        this.host = host;
        System.out.println("Parser : " + input);

        // USE
        if (input.toUpperCase().contains("USE")) {
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
        if (input.toUpperCase().contains("CREATE DATABASE")) {
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
                host.setCurrentDatabase(databaseName);
            }
        }

        // DROP DATABASE
        if (input.toUpperCase().contains("DROP DATABASE")) {
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
        if (input.toUpperCase().contains("CREATE TABLE")) {
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
        if (input.toUpperCase().contains("DROP TABLE")) {
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

        // CRERATE INDEX indexname ON tablename (columnname,...)
        if (input.toUpperCase().contains("CREATE INDEX")) {
            System.out.println("CREATE INDEX");
            String[] split = input.split(" ");
            if (split.length <= 4) {
                host.setError("Invalid syntax");
                return;
            }
            if (!split[3].equalsIgnoreCase("ON")) {
                host.setError("Invalid syntax");
                return;
            }

            String indexName = split[2];
            String tableName = split[4];

            try {
                Reader reader = new FileReader("Catalog.json");
                JSONParser jsonParser = new JSONParser();
                JSONObject catalog = (JSONObject) jsonParser.parse(reader);
                reader.close();
            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }

            StringBuilder contents = new StringBuilder();
            for (int i = 5; i < split.length; i++) {
                contents.append(split[i]).append(" ");
            }

            // contents = (columnname,...)
            // check for ( and )
            System.out.println("Contents: " + contents);

            System.out.println("elso " + contents.toString().charAt(0));
            System.out.println("utolso " + contents.toString().charAt(contents.length() - 2));

            if (!(contents.toString().charAt(0) == '(') || !(contents.toString().charAt(contents.length() - 2) == ')')) {
                host.setError("Invalid syntax");
                return;
            }

            new CreateIndex(indexName, tableName, contents.toString(), currentDatabase.trim(), this);
            if (otherError.equals("")) {
                host.setError("");
            } else {
                host.setError(otherError);
                otherError = "";
            }
        }

        // INSERT INTO tablename VALUES (value1, value2, ...)
        if (input.toUpperCase().contains("INSERT INTO")) {
            System.out.println("INSERT INTO");
            String[] split = input.split(" ");
//            for (int i = 0; i < split.length; i++) {
//                System.out.println("split[" + i + "] = " + split[i]);
//            }
            if (split.length <= 4) {
                host.setError("Invalid syntax: INSERT INTO tablename VALUES (value1, value2, ...)");
                return;
            }
            if (!split[3].equalsIgnoreCase("VALUES")) {
                host.setError("Invalid syntax: INSERT INTO");
                return;
            }

            String tableName = split[2];
            String contents = split[4];
            if (contents.charAt(0) != '(' || contents.charAt(contents.length() - 1) != ')') {
                host.setError("Invalid syntax: VALUES()");
                return;
            }
            contents = contents.substring(1, contents.length() - 1);

            new InsertInto(currentDatabase, tableName, contents, this);
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
