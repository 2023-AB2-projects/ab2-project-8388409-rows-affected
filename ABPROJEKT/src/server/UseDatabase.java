package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class UseDatabase {

    public UseDatabase(String currentDatabase,Parser parser) {

        JSONObject catalog;
        try {
            Reader reader = new FileReader("Catalog.json");
            JSONParser jsonParser = new JSONParser();
            catalog = (JSONObject) jsonParser.parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        JSONArray databases = (JSONArray) catalog.get("Databases");

        for (Object database : databases) {
            JSONObject databaseObject = (JSONObject) database;
            JSONObject databaseContents = (JSONObject) databaseObject.get("Database");
            String databaseNameInCatalog = (String) databaseContents.get("_dataBaseName");
            if (databaseNameInCatalog.equals(currentDatabase)) {
                parser.setParserError(true);
                return;
            }
        }
    }
}
