package server;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class CreateDatabase {
    public CreateDatabase(String databaseName, String contents) {
        JSONObject catalog = new JSONObject();
        try {
            Reader reader = new FileReader("Catalog.json");
            catalog = (JSONObject) new JSONParser().parse(reader);
            reader.close();
        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }
        JSONObject database = new JSONObject();
        JSONArray tables = new JSONArray();
        database.put("_dataBaseName", databaseName);
        database.put("Tables", tables);
        JSONArray databases = (JSONArray) catalog.get("Databases");
        databases.add(database);
        catalog.put("Databases", databases);
        try {
            FileWriter fileWriter = new FileWriter("Catalog.json");
            fileWriter.write(catalog.toJSONString());
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
