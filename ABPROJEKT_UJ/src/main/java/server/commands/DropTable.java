package server.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import server.Parser;

import javax.swing.text.Element;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class DropTable {
    public DropTable(String tableName, String databaseName, Parser parser) {
        JSONObject catalog;
        try {
            Reader reader = new FileReader("Catalog.json");
            JSONParser jsonParser = new JSONParser();
            catalog = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject myDatabase = null;
        JSONArray databases = (JSONArray) catalog.get("Databases");

        for (int i = 0; i < databases.size(); i++) {
            JSONObject database = (JSONObject) databases.get(i);
            JSONObject databaseContents = (JSONObject) database.get("Database");
            String databaseNameInCatalog = (String) databaseContents.get("_dataBaseName");
            if (databaseNameInCatalog.equals(databaseName)) {
                myDatabase = database;
                break;
            }
        }
        if (myDatabase == null) {
            parser.setOtherError("Database does not exist");
            return;
        }

        // remove table from database
        JSONObject databaseContents = (JSONObject) myDatabase.get("Database");
        JSONArray tables = (JSONArray) databaseContents.get("Tables");
        JSONObject tableToRemove = null;
        for (int i = 0; i < tables.size(); i++) {
            JSONObject table = (JSONObject) tables.get(i);
            JSONObject tableContents = (JSONObject) table.get("Table");
            String tableNameInCatalog = (String) tableContents.get("_tableName");
            System.out.println(tableNameInCatalog + " " + tableName);
            if (tableNameInCatalog.equals(tableName)) {
                System.out.println("Removing table");
                tableToRemove = table;
                break;
            }
        }
        if (tableToRemove != null) {
            tables.remove(tableToRemove);
        }
        else {
            parser.setOtherError("Table does not exist");
        }
    }
}
