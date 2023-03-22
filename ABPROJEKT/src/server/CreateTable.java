package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CreateTable {
    public CreateTable(String tableName, String databaseName, String contents) {
        JSONObject catalog;
        try {
            Reader reader = new FileReader("Catalog.json");
            JSONParser jsonParser = new JSONParser();
            catalog = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONObject myDatabase = new JSONObject();
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



    }
}
